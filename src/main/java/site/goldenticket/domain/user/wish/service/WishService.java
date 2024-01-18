package site.goldenticket.domain.user.wish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.user.wish.entity.WishProduct;
import site.goldenticket.domain.user.wish.repository.WishProductRepository;

import java.util.List;

import static site.goldenticket.common.response.ErrorCode.WISH_PRODUCT_NOT_FOUND;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WishService {

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
    public void deleteWishProduct(Long id) {
        WishProduct wishProduct = findProductById(id);
        wishProductRepository.delete(wishProduct);
    }

    private WishProduct findProductById(Long id) {
        return wishProductRepository.findById(id)
                .orElseThrow(() -> new CustomException(WISH_PRODUCT_NOT_FOUND));
    }

    private WishProduct createWishProduct(Long userId, Product product) {
        return WishProduct.builder()
                .userId(userId)
                .product(product)
                .build();
    }
}
