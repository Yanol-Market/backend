package site.goldenticket.domain.product.wish.service;

import static site.goldenticket.common.response.ErrorCode.WISH_PRODUCT_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.product.wish.entity.WishProduct;
import site.goldenticket.domain.product.wish.repository.WishProductRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WishProductService {

    private final WishProductRepository wishProductRepository;
    private final ProductService productService;

    public List<WishProduct> findWishProduct(Long userId) {
        log.info("Find Wish Product User Id = {}", userId);
        return wishProductRepository.findByUserIdWithProduct(userId);
    }

    @Transactional
    public WishProduct saveWishProduct(Long userId, Long productId) {
        Product product = productService.getProduct(productId);
        log.info("Save User Id ={}, Product = {}", userId, product);

        WishProduct wishProduct = createWishProduct(userId, product);
        wishProductRepository.save(wishProduct);
        log.info("Save Wish Sequence Id = {}", wishProduct.getId());

        return wishProduct;
    }

    @Transactional
    public void deleteWishProduct(Long userId, Long productId) {
        WishProduct wishProduct = findByUserIdAndProductId(userId, productId);
        wishProductRepository.delete(wishProduct);
    }

    private WishProduct findByUserIdAndProductId(Long userId, Long productId) {
        return wishProductRepository.findByUserIdAndProductId(userId, productId)
            .orElseThrow(() -> new CustomException(WISH_PRODUCT_NOT_FOUND));
    }

    private WishProduct createWishProduct(Long userId, Product product) {
        return WishProduct.builder()
            .userId(userId)
            .product(product)
            .build();
    }
}
