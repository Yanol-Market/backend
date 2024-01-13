package site.goldenticket.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.constants.AreaCode;
import site.goldenticket.common.constants.PriceRange;
import site.goldenticket.common.constants.ProductStatus;
import site.goldenticket.common.constants.ReservationStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.pagination.constants.PaginationConstants;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.domain.product.dto.*;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.common.pagination.slice.CustomSlice;
import site.goldenticket.domain.product.repository.ProductRepository;
import site.goldenticket.domain.reservation.model.Reservation;
import site.goldenticket.domain.reservation.service.ReservationService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static site.goldenticket.common.response.ErrorCode.PRODUCT_ALREADY_EXISTS;
import static site.goldenticket.common.response.ErrorCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ReservationService reservationService;
    private final RedisService redisService;

    @Transactional(readOnly = true)
    public Slice<SearchProductResponse> getProductsBySearch(
            AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate, PriceRange priceRange,
            LocalDate cursorCheckInDate, Long cursorId, Pageable pageable
    ) {
        CustomSlice<Product> productSlice = productRepository.getProductsBySearch(
                areaCode, keyword, checkInDate, checkOutDate, priceRange, cursorCheckInDate, cursorId, pageable
        );

        SearchProductResponse searchProductResponse = SearchProductResponse.fromEntity(
                areaCode, keyword, checkInDate, checkOutDate, priceRange, productSlice.getTotalElements(), productSlice
        );

        return new SliceImpl<>(
                Collections.singletonList(searchProductResponse),
                pageable,
                productSlice.hasNext()
        );
    }

    @Transactional(readOnly = true)
    public Slice<RegionProductResponse> getProductsByAreaCode(
            AreaCode areaCode, LocalDate cursorCheckInDate, Long cursorId, Pageable pageable
    ) {
        CustomSlice<Product> productSlice = productRepository.getProductsByAreaCode(
                areaCode, cursorCheckInDate, cursorId, pageable
        );

        RegionProductResponse regionProductResponse = RegionProductResponse.fromEntity(
                productSlice.getTotalElements(), productSlice
        );

        return new SliceImpl<>(
                Collections.singletonList(regionProductResponse),
                pageable,
                productSlice.hasNext()
        );
    }

    @Transactional
    public Long createProduct(ProductRequest productRequest, Long reservationId) {
        // TODO : 사용자 식별자 값 저장
        Long userId = 1L;

        Reservation reservation = reservationService.getReservation(reservationId);

        if (ReservationStatus.REGISTERED.equals(reservation.getReservationStatus())) {
            throw new CustomException(PRODUCT_ALREADY_EXISTS);
        }

        reservation.setReservationStatus(ReservationStatus.REGISTERED);
        reservationService.saveReservation(reservation);

        return productRepository.save(productRequest.toEntity(reservation, reservationId, userId)).getId();
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetails(Long productId) {
        Product product = getProduct(productId);

        // TODO : 사용자 별 이메일로 키 값 설정 하기
        // TODO : 로그인 및 비로그인 사용자 모두 한 번만 조회수 증가가 일어날 수 있도록 고유 키 값 고민
        String userKey = "test@email.com";
        updateProductViewCount(userKey, productId.toString());

        return ProductDetailResponse.fromEntity(product);
    }

    @Transactional
    public Long updateProduct(ProductRequest productRequest, Long productId) {
        Product product = getProduct(productId);
        product.update(productRequest.getGoldenPrice(), productRequest.getContent());
        return productRepository.save(product).getId();
    }

    @Transactional
    public Long deleteProduct(Long productId) {
        Product product = getProduct(productId);
        productRepository.delete(product);

        Reservation reservation = reservationService.getReservation(product.getReservationId());
        reservation.setReservationStatus(ReservationStatus.NOT_REGISTERED);
        reservationService.saveReservation(reservation);

        return productId;
    }
    
    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }

    private void updateProductViewCount(String userKey, String productKey) {
        String viewProductKey = userKey.concat(":").concat("viewProductList");

        List<String> viewProductList = redisService.getList(viewProductKey, String.class);

        // 1. 사용자 조회 이력에 해당 상품이 포함되어 있지 않으면
        if (!viewProductList.contains(productKey)) {

            // 1-1. 사용자 조회 이력에 해당 상품 추가
            redisService.leftPush(viewProductKey, productKey);
            log.info("User View Product List 추가. 사용자 이메일: {}, 조회 상품 아이디: {}", userKey, productKey);

            // 1-2. 해당 상품의 조회수 가져온 후 증가
            Double currentViewCount = redisService.getZScore("viewCountRanking", productKey);
            Double updateViewCount = (currentViewCount != null) ? currentViewCount + 1 : 1;
            redisService.addZScore("viewCountRanking", productKey, updateViewCount);
            log.info("Product ViewCount Update 완료. 상품 아이디: {}, 조회수: {}", productKey, updateViewCount);
        }
        // 2. 이미 조회한 이력이 있을 경우 실행 하지 않음
    }

    @Transactional
    public void updateViewCounts() {
        List<Product> productList = productRepository.findAll();

        for (Product product : productList) {
            String productKey = String.valueOf(product.getId());

            Double currentViewCount = redisService.getZScore("viewCountRanking", productKey);
            product.setViewCount(Integer.parseInt(String.valueOf(currentViewCount)));

            log.info("Product ViewCount Update 완료. 상품 아이디: {}, 조회수: {}", product.getId(), currentViewCount);
        }

        productRepository.saveAll(productList);
    }

    @Transactional
    public void updateProductStatus() {
        List<Product> productList = productRepository.findAll();
        LocalDate currentDate = LocalDate.now();

        for (Product product : productList) {
            LocalDate checkInDate = product.getCheckInDate();

            if (checkInDate.isBefore(currentDate)) {
                product.setProductStatus(ProductStatus.EXPIRED);

                log.info("Product Status Update 완료. 상품 ID: {}, 상태: {}", product.getId(), product.getProductStatus());
            }
        }

        productRepository.saveAll(productList);
    }

    public HomeProductResponse getHomeProduct() {
        Pageable pageable = PageRequest.of(PaginationConstants.DEFAULT_PAGE, PaginationConstants.MIN_PAGE_SIZE);

        List<ProductResponse> goldenPriceTop5 = getProductResponses(productRepository.findTop5ByGoldenPriceAsc(pageable));
        List<ProductResponse> viewCountTop5 = getProductResponses(productRepository.findTop5ByViewCountDesc(pageable));
        List<ProductResponse> recentRegisteredTop5 = getProductResponses(productRepository.findTop5ByIdDesc(pageable));
        List<ProductResponse> dayUseTop5 = getProductResponses(productRepository.findTop5DayUseProductsCheckInDateAsc(pageable));

        return HomeProductResponse.builder()
                .goldenPriceTop5(goldenPriceTop5)
                .viewCountTop5(viewCountTop5)
                .recentRegisteredTop5(recentRegisteredTop5)
                .dayUseTop5(dayUseTop5)
                .build();
    }

    private List<ProductResponse> getProductResponses(List<Product> productList) {
        return productList.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
