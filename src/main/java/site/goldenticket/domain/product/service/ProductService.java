package site.goldenticket.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.constants.ProductStatus;
import site.goldenticket.common.constants.ReservationStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.domain.product.dto.ProductDetailResponse;
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;
import site.goldenticket.domain.reservation.model.Reservation;
import site.goldenticket.domain.reservation.service.ReservationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public ProductDetailResponse getProductDetails(Long productId) {
        Product product = getProduct(productId);

        // TODO: 적절한 사용자 이메일 값을 얻어와서 사용
        String userEmail = "test@email.com";
        updateProductViews(userEmail, productId.toString());

        return ProductDetailResponse.fromEntity(product);
    }

    @Transactional
    public Long createProduct(ProductRequest productRequest, Long reservationId) {
        Reservation reservation = reservationService.getReservation(reservationId);
        checkReservationStatus(reservation.getReservationStatus());

        reservation.setReservationStatus(ReservationStatus.REGISTERED);
        reservationService.saveReservation(reservation);

        return productRepository.save(productRequest.toEntity(reservation, reservationId)).getId();
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

    private void checkReservationStatus(ReservationStatus reservationStatus) {
        if (ReservationStatus.REGISTERED.equals(reservationStatus)) {
            throw new CustomException(PRODUCT_ALREADY_EXISTS);
        }
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }

    private void updateProductViews(String userKey, String productKey) {
        // 사용자가 조회한 상품 목록 가져오기
        Map<String, List<String>> userViewMap = redisService.getMap(userKey, String.class);

        // 1. 사용자의 조회 이력에 해당 상품이 포함되어 있지 않으면
        if (!userViewMap.containsKey("viewProduct") || !userViewMap.get("viewProduct").contains(productKey)) {

            // 1-1. 사용자의 조회 이력에 해당 상품 추가 ( 누적 증가 방지 )
            List<String> viewedProductList = userViewMap.computeIfAbsent("viewProduct", k -> new ArrayList<>());
            viewedProductList.add(productKey);
            redisService.setMap(userKey, userViewMap);

            // 1-2. 해당 상품의 조회수 가져온 후 증가
            int viewCount = redisService.get(productKey, String.class).map(Integer::parseInt).orElse(0) + 1;

            // 1-3. 조회수 업데이트
            redisService.set(productKey, String.valueOf(viewCount), 86400L);
        }
        // 2. 이미 조회한 상품이라면 조회수 업데이트를 수행하지 않음
    }

    @Transactional
    public void updateViewCounts() {
        List<Product> productList = productRepository.findAll();

        for (Product product : productList) {
            String productKey = String.valueOf(product.getId());

            int dailyViewCount = redisService.get(productKey, String.class).map(Integer::parseInt).orElse(0);
            int updatedViewCount = product.getViewCount() + dailyViewCount;

            product.setViewCount(updatedViewCount);
            redisService.delete(productKey);

            log.info("Product ViewCount Update 완료. 상품 ID: {}, 조회수: {}", product.getId(), updatedViewCount);
        }

        productRepository.saveAll(productList);
    }

    @Transactional
    public void updateProductStatus() {
        List<Product> productList = productRepository.findAll();
        LocalDate currentDate = LocalDate.now();

        for (Product product : productList) {
            LocalDate checkInDate = product.getCheckInDate();

            if (currentDate.isBefore(checkInDate)) {
                product.setProductStatus(ProductStatus.EXPIRED);

                log.info("Product Status Update 완료. 상품 ID: {}, 상태: {}", product.getId(), product.getProductStatus());
            }
        }

        productRepository.saveAll(productList);
    }
}
