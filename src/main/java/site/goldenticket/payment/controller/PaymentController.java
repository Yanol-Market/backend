package site.goldenticket.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.payment.dto.response.PaymentDetailResponse;
import site.goldenticket.payment.dto.response.PaymentReadyResponse;
import site.goldenticket.payment.service.PaymentService;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse<PaymentDetailResponse>> getPaymentDetail(@PathVariable final Long productId) {
        PaymentDetailResponse paymentDetail = paymentService.getPaymentDetail(productId);
        return ResponseEntity.ok(CommonResponse.ok("Payment details retrieved successfully", paymentDetail));
    }

    @PostMapping("/{productId}/prepare")
    public ResponseEntity<CommonResponse<PaymentReadyResponse>> preparePayment(@PathVariable final Long productId) {
        PaymentReadyResponse paymentReadyResponse = paymentService.preparePayment(productId);
        return ResponseEntity.ok(CommonResponse.ok("Payment ready successfully", paymentReadyResponse));
    }
}
