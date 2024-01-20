package site.goldenticket.domain.user.wish.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.user.wish.service.WishRegionService;
import site.goldenticket.domain.user.wish.dto.WishRegionCreateRequest;
import site.goldenticket.domain.user.wish.dto.WishRegionListResponse;
import site.goldenticket.domain.user.wish.dto.WishRegionResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wish-regions")
public class WishRegionController {

    private final WishRegionService wishRegionService;

    @PostMapping
    public ResponseEntity<CommonResponse<WishRegionResponse>> createWishRegion(@Valid @RequestBody
    WishRegionCreateRequest wishRegionCreateRequest) {
        Long userId = 1L; //시큐리티 적용 후 수정 예정
        return ResponseEntity.ok(CommonResponse.ok("관심지역이 등록되었습니다.",
            wishRegionService.createWishRegion(userId, wishRegionCreateRequest)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<WishRegionListResponse>> getWishRegionList() {
        Long userId = 1L; //시큐리티 적용 후 수정 예정
        return ResponseEntity.ok(CommonResponse.ok("관심 지역 목록이 조회되었습니다.",
            wishRegionService.getWishRegionList(userId)));
    }

}
