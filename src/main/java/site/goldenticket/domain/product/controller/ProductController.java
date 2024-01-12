package site.goldenticket.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.constants.AreaCode;
import site.goldenticket.common.pagination.constants.PaginationConstants;
import site.goldenticket.common.constants.PriceRange;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.product.dto.ProductDetailResponse;
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.dto.SearchProductResponse;
import site.goldenticket.domain.product.service.ProductService;

import java.time.LocalDate;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<CommonResponse<Slice<SearchProductResponse>>> getProductsBySearch(
            @RequestParam(required = false) AreaCode areaCode,
            @RequestParam String keyword,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate,
            @RequestParam(required = false) PriceRange priceRange,
            @RequestParam(required = false) LocalDate cursorCheckInDate,
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(
                    page = PaginationConstants.DEFAULT_PAGE,
                    size = PaginationConstants.DEFAULT_PAGE_SIZE
            ) Pageable pageable
    ) {
        Slice<SearchProductResponse> searchProductResponseSlice = productService.getProductsBySearch(
                areaCode, keyword, checkInDate, checkOutDate, priceRange, cursorCheckInDate, cursorId, pageable
        );

        return ResponseEntity.ok(CommonResponse.ok("상품이 성공적으로 조회가 완료되었습니다.", searchProductResponseSlice));
    }

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
