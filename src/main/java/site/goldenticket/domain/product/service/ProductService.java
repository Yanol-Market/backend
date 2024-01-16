package site.goldenticket.domain.product.service;

import static site.goldenticket.common.response.ErrorCode.PRODUCT_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public Product findProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }
    public List<Product> findProductListByUserId(Long userId) {
        return productRepository.findAllByUserId(userId);
    }
}
