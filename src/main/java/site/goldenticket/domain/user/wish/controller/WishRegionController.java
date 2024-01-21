package site.goldenticket.domain.user.wish.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.wish.dto.WishRegionRegisterRequest;
import site.goldenticket.domain.user.wish.dto.WishRegionsResponse;
import site.goldenticket.domain.user.wish.entity.WishRegion;
import site.goldenticket.domain.user.wish.service.WishRegionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/regions")
public class WishRegionController {

    private final WishRegionService wishRegionService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> registerWishRegion(
            @Valid @RequestBody WishRegionRegisterRequest wishRegionRegisterRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();
        wishRegionService.registerWishRegion(userId, wishRegionRegisterRequest);
        return ResponseEntity.ok(CommonResponse.ok());
    }

    @GetMapping
    public ResponseEntity<CommonResponse<WishRegionsResponse>> getWishRegion(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();
        List<WishRegion> response = wishRegionService.findWishRegion(userId);
        return ResponseEntity.ok(CommonResponse.ok(WishRegionsResponse.from(response)));
    }
}
