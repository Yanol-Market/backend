package site.goldenticket.domain.user.wish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.user.wish.entity.Wish;
import site.goldenticket.domain.user.wish.repository.WishRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;

    @Transactional
    public Long saveWishProduct(Long userId, Long productId) {
        Product product = productService.getProduct(productId);
        log.info("Save User Id ={}, Product = {}", userId, product);

        Wish wish = createWish(userId, product);
        wishRepository.save(wish);
        log.info("Save Wish Sequence Id = {}", wish.getId());

        return wish.getId();
    }

    private Wish createWish(Long userId, Product product) {
        return Wish.builder()
                .userId(userId)
                .product(product)
                .build();
    }
}
