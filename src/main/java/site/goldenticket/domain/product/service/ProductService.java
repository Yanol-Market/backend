package site.goldenticket.domain.product.service;

import static site.goldenticket.common.redis.constants.RedisConstants.SCORE_INCREMENT_AMOUNT;
import static site.goldenticket.common.redis.constants.RedisConstants.VIEW_RANKING_KEY;
import static site.goldenticket.common.response.ErrorCode.PRODUCT_ALREADY_EXISTS;
import static site.goldenticket.common.response.ErrorCode.PRODUCT_NOT_FOUND;
import static site.goldenticket.common.response.ErrorCode.RESERVATION_NOT_FOUND;
import static site.goldenticket.domain.product.constants.DummyUrlConstants.*;
import static site.goldenticket.dummy.reservation.constants.ReservationStatus.NOT_REGISTERED;
import static site.goldenticket.dummy.reservation.constants.ReservationStatus.REGISTERED;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import site.goldenticket.common.api.RestTemplateService;
import site.goldenticket.common.constants.PaginationConstants;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.dto.HomeProductResponse;
import site.goldenticket.domain.product.dto.ProductDetailResponse;
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.dto.ProductResponse;
import site.goldenticket.domain.product.dto.RegionProductResponse;
import site.goldenticket.domain.product.dto.SearchProductResponse;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.CustomSlice;
import site.goldenticket.domain.product.repository.ProductRepository;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.dummy.reservation.dto.ReservationDetailsResponse;
import site.goldenticket.dummy.reservation.dto.ReservationResponse;
import site.goldenticket.dummy.reservation.dto.UpdateReservationStatusRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final RestTemplateService restTemplateService;

    private final RedisService redisService;
    private final ProductRepository productRepository;

    // 1. 키워드 검색 및 지역 검색 메서드
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

    // 2. 야놀자 예약 정보 조회 메서드
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations(Long yaUserId) {
        String getUrl = buildReservationUrl(RESERVATIONS_ENDPOINT, yaUserId);

        return restTemplateService.getList(
                getUrl,
                ReservationResponse[].class
        );
    }

    // 3. 상품 생성, 조회, 수정, 삭제 관련 메서드
    @Transactional
    public ProductResponse createProduct(
            ProductRequest productRequest, Long reservationId, PrincipalDetails principalDetails
    ) {
        if (productRepository.existsByReservationId(reservationId)) {
            throw new CustomException(PRODUCT_ALREADY_EXISTS);
        }

        String getUrl = buildReservationUrl(RESERVATION_ENDPOINT, reservationId);

        ReservationDetailsResponse reservationDetailsResponse = restTemplateService.get(
                getUrl,
                ReservationDetailsResponse.class
        ).orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

        Product product = productRequest.toEntity(reservationDetailsResponse, principalDetails.getUserId());
        Product savedProduct = productRepository.save(product);

        String updateUrl = buildReservationUrl(RESERVATION_UPDATE_STATUS_ENDPOINT, reservationId);

        restTemplateService.put(updateUrl, new UpdateReservationStatusRequest(REGISTERED));

        return ProductResponse.fromEntity(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(
            Long productId, PrincipalDetails principalDetails, HttpServletRequest request, HttpServletResponse response
    ) {
        Product product = getProduct(productId);

        String userKey = (principalDetails != null) ? principalDetails.getUsername() : generateOrRetrieveAnonymousKey(request, response);
        boolean isSeller = principalDetails != null && principalDetails.getUserId().equals(product.getUserId());

        updateProductViewCount(userKey, productId.toString());

        return ProductDetailResponse.fromEntity(product, isSeller);
    }

    @Transactional
    public ProductResponse updateProduct(ProductRequest productRequest, Long productId) {
        Product product = getProduct(productId);
        product.update(productRequest.goldenPrice(), productRequest.content());
        Product updatedProduct = productRepository.save(product);

        return ProductResponse.fromEntity(updatedProduct);
    }

    @Transactional
    public ProductResponse deleteProduct(Long productId) {
        Product product = getProduct(productId);
        productRepository.delete(product);

        String updateUrl = buildReservationUrl(RESERVATION_UPDATE_STATUS_ENDPOINT, product.getReservationId());

        restTemplateService.put(updateUrl, new UpdateReservationStatusRequest(NOT_REGISTERED));

        return ProductResponse.fromEntity(product);
    }

    // 4. 기타 유틸 메서드
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    private String buildReservationUrl(String endpoint, Long pathVariable) {
        return UriComponentsBuilder
                .fromUriString(DISTRIBUTE_BASE_URL)
                .path(endpoint)
                .buildAndExpand(pathVariable)
                .encode(StandardCharsets.UTF_8)
                .toUriString();
    }

    private String generateOrRetrieveAnonymousKey(HttpServletRequest request, HttpServletResponse response) {
        String anonymousKey = Arrays.stream(request.getCookies())
                .filter(
                        cookie -> "AnonymousKey".equals(cookie.getName())
                )
                .map(
                        cookie -> cookie.getValue()
                )
                .findFirst()
                .orElse(null);

        if (anonymousKey == null) {
            ResponseCookie cookie = ResponseCookie.from("AnonymousKey", UUID.randomUUID().toString())
                    .domain(".golden-ticket.site")
                    .httpOnly(false)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());
        }

        return anonymousKey;
    }

    private void updateProductViewCount(String userKey, String productKey) {
        String viewProductKey = userKey.concat(":").concat("viewProductList");

        List<String> viewProductList = redisService.getList(viewProductKey, String.class);

        if (!viewProductList.contains(productKey)) {
            redisService.leftPush(viewProductKey, productKey);

            Double currentViewCount = redisService.getZScore(VIEW_RANKING_KEY, productKey);
            Double updateViewCount = (currentViewCount != null) ? SCORE_INCREMENT_AMOUNT + 1 : SCORE_INCREMENT_AMOUNT;
            redisService.addZScore(VIEW_RANKING_KEY, productKey, updateViewCount);
        }
    }

    public HomeProductResponse getHomeProduct() {
        Pageable pageable = PageRequest.of(PaginationConstants.DEFAULT_PAGE, PaginationConstants.MIN_PAGE_SIZE);

        List<ProductResponse> goldenPriceTop5 = getProductResponseList(productRepository.findTop5ByGoldenPriceAsc(pageable));
        List<ProductResponse> viewCountTop5 = getProductResponseList(productRepository.findTop5ByViewCountDesc(pageable));
        List<ProductResponse> recentRegisteredTop5 = getProductResponseList(productRepository.findTop5ByIdDesc(pageable));
        List<ProductResponse> dayUseTop5 = getProductResponseList(productRepository.findTop5DayUseProductsCheckInDateAsc(pageable));

        return new HomeProductResponse(
                goldenPriceTop5,
                viewCountTop5,
                recentRegisteredTop5,
                dayUseTop5
        );
    }

    private List<ProductResponse> getProductResponseList(List<Product> productList) {
        return productList.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<Product> findProductListByUserId(Long userId) {
        return productRepository.findAllByUserId(userId);
    }

    @Transactional
    public void updateProductForNego(Product product) {
        productRepository.save(product);
    }
    public List<Product> getSoldOutProducts() {
        return productRepository.findByStatus(ProductStatus.SOLD_OUT);
    }
}
