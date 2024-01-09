package site.goldenticket.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.service.ProductService;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/{reservationId}")
    public ResponseEntity<CommonResponse<Long>> createProduct(
            @PathVariable Long reservationId,
            @RequestBody ProductRequest productRequest
    ) {
        return ResponseEntity.ok(CommonResponse.ok("상품이 성공적으로 등록이 완료되었습니다.", productService.createProduct(productRequest, reservationId)));
    }
}
