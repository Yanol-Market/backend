package site.goldenticket.domain.user.wish.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.wish.service.WishService;
import site.goldenticket.domain.user.wish.dto.WishRegionRegisterRequest;
import site.goldenticket.domain.user.wish.dto.WishRegionListResponse;
import site.goldenticket.domain.user.wish.dto.WishRegionResponse;

//@RestController
@RequiredArgsConstructor
@RequestMapping("/users/regions")
public class WishController {

    private final WishService wishService;

    @PostMapping
    public ResponseEntity<CommonResponse<WishRegionResponse>> registerWishRegion(
            @Valid @RequestBody WishRegionRegisterRequest wishRegionRegisterRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();
        WishRegionResponse response = wishService.createWishRegion(userId, wishRegionRegisterRequest);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<WishRegionListResponse>> getWishRegionList(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();
        WishRegionListResponse response = wishService.getWishRegionList(userId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

}
