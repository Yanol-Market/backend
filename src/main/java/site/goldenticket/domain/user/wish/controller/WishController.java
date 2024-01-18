package site.goldenticket.domain.user.wish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.wish.dto.WishProductSaveRequest;
import site.goldenticket.domain.user.wish.dto.WishProductSaveResponse;
import site.goldenticket.domain.user.wish.dto.WishProductsResponse;
import site.goldenticket.domain.user.wish.entity.WishProduct;
import site.goldenticket.domain.user.wish.service.WishService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/wishes")
public class WishController {

    private final WishService wishService;

    @GetMapping("/product")
    public ResponseEntity<CommonResponse<WishProductsResponse>> getWishProducts(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();
        List<WishProduct> response = wishService.findWishProduct(userId);
        return ResponseEntity.ok(CommonResponse.ok(WishProductsResponse.of(response)));
    }

    @PostMapping("/product")
    public ResponseEntity<CommonResponse<WishProductSaveResponse>> saveWishProduct(
            @RequestBody WishProductSaveRequest wishProductSaveRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();
        WishProduct response = wishService.saveWishProduct(userId, wishProductSaveRequest.productId());
        return ResponseEntity.ok(CommonResponse.ok(WishProductSaveResponse.of(response)));
    }

    @DeleteMapping("/product/{wishId}")
    public ResponseEntity<CommonResponse<Void>> deleteWishProduct(@PathVariable Long wishId) {
        wishService.deleteWishProduct(wishId);
        return ResponseEntity.ok(CommonResponse.ok());
    }
}
