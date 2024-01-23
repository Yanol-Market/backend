package site.goldenticket.domain.nego.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.HandoverResponse;
import site.goldenticket.domain.nego.dto.response.NegoAvailableResponse;
import site.goldenticket.domain.nego.dto.response.NegoListResponse;
import site.goldenticket.domain.nego.dto.response.NegoResponse;
import site.goldenticket.domain.nego.dto.response.PayResponse;
import site.goldenticket.domain.nego.dto.response.PriceProposeResponse;
import site.goldenticket.domain.nego.service.NegoService;
import site.goldenticket.domain.security.PrincipalDetails;

@RestController
@RequestMapping("/nego")
@RequiredArgsConstructor

public class NegoController {

    private final NegoService negoService;

    @PostMapping("/proposePrice/{productId}")
    public ResponseEntity<CommonResponse<PriceProposeResponse>> proposePrice(@RequestBody PriceProposeRequest request, @PathVariable Long productId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        PriceProposeResponse response = negoService.proposePrice(productId, request, principalDetails);
        return ResponseEntity.ok(CommonResponse.ok("네고가 전달되었습니다.", response));
    }

    @PatchMapping("/confirm/{negoId}")
    public ResponseEntity<CommonResponse<NegoResponse>> confirmPrice(@PathVariable Long negoId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        NegoResponse response = negoService.confirmPrice(negoId, principalDetails);
        return ResponseEntity.ok(CommonResponse.ok("네고가 승인되었습니다", response));
    }

    @PatchMapping("/deny/{negoId}")
    public ResponseEntity<CommonResponse<NegoResponse>> denyPrice(@PathVariable Long negoId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        NegoResponse response = negoService.denyPrice(negoId, principalDetails);
        return ResponseEntity.ok(CommonResponse.ok("네고가 거절되었습니다", response));
    }

    @PatchMapping("/pay/{negoId}")
    public ResponseEntity<CommonResponse<PayResponse>> pay(@PathVariable Long negoId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        PayResponse payResponse = negoService.pay(negoId, principalDetails);
        return ResponseEntity.ok(CommonResponse.ok("결제가 진행됩니다", payResponse));
    }

    @PostMapping("/handoverProduct/{productId}")
    public ResponseEntity<CommonResponse<HandoverResponse>> handoverProduct(@PathVariable Long productId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        HandoverResponse handoverResponse = negoService.handOverProduct(productId, principalDetails);
        return ResponseEntity.ok(CommonResponse.ok("양도가 완료되었습니다", handoverResponse));
    }

    @PatchMapping("/denyhandoverProduct/{productId}")
    public ResponseEntity<CommonResponse<NegoResponse>> denyHandoverProduct(@PathVariable Long productId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        NegoResponse response = negoService.denyHandoverProduct(productId, principalDetails);
        return ResponseEntity.ok(CommonResponse.ok("양도가 거절되었습니다", response));
    }

    @GetMapping("/available")
    public ResponseEntity<CommonResponse<NegoAvailableResponse>> getNegoAvailable(
        @RequestParam(name = "productId") Long productId,
        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(CommonResponse.ok("네고 가능 여부가 조회되었습니다",
            negoService.isAvailableNego(principalDetails.getUserId(), productId)));
    }

    @GetMapping("/test")
    public ResponseEntity<CommonResponse<NegoListResponse>> getNegoListForTest() {
        return ResponseEntity.ok(
            CommonResponse.ok("(테스트용) 모든 네고 기록이 조회되었습니다.", negoService.getNegoListForTest()));
    }
}
