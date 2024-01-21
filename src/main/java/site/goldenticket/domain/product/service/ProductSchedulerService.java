package site.goldenticket.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;

import java.time.LocalDate;
import java.util.List;

import static site.goldenticket.common.redis.constants.RedisConstants.VIEW_RANKING_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSchedulerService {

    private final RedisService redisService;
    private final ProductRepository productRepository;

    @Transactional
    public void updateViewCounts() {
        List<Product> productList = productRepository.findAll();

        for (Product product : productList) {
            String productKey = String.valueOf(product.getId());

            Double currentViewCount = redisService.getZScore(VIEW_RANKING_KEY, productKey);

            if (currentViewCount != null) {
                int intValue = (int) Math.floor(currentViewCount);
                product.setViewCount(intValue);

                log.info("Product ViewCount Update 완료. 상품 아이디: {}, 조회수: {}", product.getId(), intValue);
            } else {
                log.warn("상품 ID {}의 현재 조회수가 존재하지 않습니다.", product.getId());
            }
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
}
