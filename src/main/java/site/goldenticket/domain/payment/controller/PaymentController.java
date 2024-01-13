package site.goldenticket.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.payment.dto.response.PaymentDetailResponse;
import site.goldenticket.domain.payment.dto.response.PaymentReadyResponse;
import site.goldenticket.domain.payment.dto.request.PaymentRequest;
import site.goldenticket.domain.payment.dto.response.PaymentResponse;
import site.goldenticket.domain.payment.service.PaymentService;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse<PaymentDetailResponse>> getPaymentDetail(@PathVariable(name = "productId") final Long productId) {
        PaymentDetailResponse paymentDetail = paymentService.getPaymentDetail(productId);
        return ResponseEntity.ok(CommonResponse.ok("Payment details retrieved successfully", paymentDetail));
    }

    @PostMapping("/{productId}/prepare")
    public ResponseEntity<CommonResponse<PaymentReadyResponse>> preparePayment(@PathVariable(name = "productId") final Long productId) {
        PaymentReadyResponse paymentReadyResponse = paymentService.preparePayment(productId);
        return ResponseEntity.ok(CommonResponse.ok("Payment ready successfully", paymentReadyResponse));
    }

    @PostMapping("/result")
    public ResponseEntity<CommonResponse<PaymentResponse>> savePayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse paymentResponse = paymentService.savePayment(request);
        return ResponseEntity.ok(CommonResponse.ok("Payment result", paymentResponse));
    }
}
