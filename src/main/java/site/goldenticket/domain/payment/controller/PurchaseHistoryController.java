package site.goldenticket.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.payment.dto.response.PurchaseCompletedDetailResponse;
import site.goldenticket.domain.payment.dto.response.PurchaseCompletedResponse;
import site.goldenticket.domain.payment.dto.response.PurchaseProgressResponse;
import site.goldenticket.domain.payment.service.PurchaseHistoryService;
import site.goldenticket.domain.security.PrincipalDetails;

import java.util.List;

@RestController
@RequestMapping("/orders/history")
@RequiredArgsConstructor
public class PurchaseHistoryController {
    private final PurchaseHistoryService purchaseHistoryService;

    @GetMapping("/progress")
    public ResponseEntity<CommonResponse<List<PurchaseProgressResponse>>> getPurchaseProgressHistory(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(CommonResponse.ok("구매중 내역이 성공적으로 조회되었습니다",purchaseHistoryService.getPurchaseProgressHistory(principalDetails)));
    }

    @GetMapping("/completed")
    public ResponseEntity<CommonResponse<List<PurchaseCompletedResponse>>> getPurchaseCompletedHistory(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(CommonResponse.ok("구매완료 내역이 성공적으로 조회되었습니다",purchaseHistoryService.getPurchaseCompletedHistory(principalDetails)));
    }

    @GetMapping("/completed/{orderId}")
    public ResponseEntity<CommonResponse<PurchaseCompletedDetailResponse>> getPurchaseCompletedHistoryDetail(
            @PathVariable(name = "orderId") final Long orderId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(CommonResponse.ok("구매완료 상세 내역이 성공적으로 조회되었습니다.",purchaseHistoryService.getPurchaseCompletedHistoryDetail(orderId,principalDetails)));
    }

    @DeleteMapping("/completed/{orderId}")
    public ResponseEntity<CommonResponse<Long>> deletePurchaseCompletedHistory(
            @PathVariable Long orderId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(CommonResponse.ok("구매완료 내역이 성공적으로 삭제되었습니다.", purchaseHistoryService.deletePurchaseCompletedHistory(orderId, principalDetails)));
    }

}
