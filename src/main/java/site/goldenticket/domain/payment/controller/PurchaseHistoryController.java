package site.goldenticket.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.domain.payment.dto.response.PurchaseCompletedDetailResponse;
import site.goldenticket.domain.payment.dto.response.PurchaseCompletedResponse;
import site.goldenticket.domain.payment.service.PurchaseHistoryService;
import site.goldenticket.domain.security.PrincipalDetails;

import java.util.List;

@RestController
@RequestMapping("/orders/history")
@RequiredArgsConstructor
public class PurchaseHistoryController {
    private final PurchaseHistoryService purchaseHistoryService;

    @GetMapping("/progress")
    public String getPurchaseProgressHistory(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return purchaseHistoryService.getPurchaseProgressHistory(principalDetails);
    }

    @GetMapping("/completed")
    public List<PurchaseCompletedResponse> getPurchaseCompletedHistory(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return purchaseHistoryService.getPurchaseCompletedHistory(principalDetails);
    }

    @GetMapping("/completed/{orderId}")
    public PurchaseCompletedDetailResponse getPurchaseCompletedHistoryDetail(
            @PathVariable(name = "orderId") final Long orderId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return purchaseHistoryService.getPurchaseCompletedHistoryDetail(orderId,principalDetails);
    }

}
