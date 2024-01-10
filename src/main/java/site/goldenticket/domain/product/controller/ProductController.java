package site.goldenticket.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.product.dto.ProductDetailResponse;
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.service.ProductService;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductDetailResponse>> getProductDetails(@PathVariable Long productId) {
        return ResponseEntity.ok(CommonResponse.ok("상품이 성공적으로 조회가 완료되었습니다.", productService.getProductDetails(productId)));
    }

    @PostMapping("/{reservationId}")
    public ResponseEntity<CommonResponse<Long>> createProduct(
            @PathVariable Long reservationId,
            @Valid @RequestBody ProductRequest productRequest
    ) {
        return ResponseEntity.ok(CommonResponse.ok("상품이 성공적으로 등록이 완료되었습니다.", productService.createProduct(productRequest, reservationId)));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<CommonResponse<Long>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest productRequest
    ) {
        return ResponseEntity.ok(CommonResponse.ok("상품이 성공적으로 수정이 완료되었습니다.", productService.updateProduct(productRequest, productId)));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CommonResponse<Long>> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(CommonResponse.ok("상품이 성공적으로 삭제가 완료되었습니다.", productService.deleteProduct(productId)));
    }
}
