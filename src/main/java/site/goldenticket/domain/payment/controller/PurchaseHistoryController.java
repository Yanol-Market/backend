package site.goldenticket.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.domain.payment.service.PurchaseHistoryService;
import site.goldenticket.domain.security.PrincipalDetails;

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
}
