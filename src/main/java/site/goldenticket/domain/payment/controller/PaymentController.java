package site.goldenticket.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.payment.dto.request.PaymentRequest;
import site.goldenticket.domain.payment.dto.response.PaymentDetailResponse;
import site.goldenticket.domain.payment.dto.response.PaymentReadyResponse;
import site.goldenticket.domain.payment.dto.response.PaymentResponse;
import site.goldenticket.domain.payment.service.PaymentService;
import site.goldenticket.domain.security.PrincipalDetails;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse<PaymentDetailResponse>> getPaymentDetail(
            @PathVariable(name = "productId") final Long productId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        PaymentDetailResponse paymentDetail = paymentService.getPaymentDetail(productId, principalDetails);
        return ResponseEntity.ok(CommonResponse.ok("Payment details retrieved successfully", paymentDetail));
    }

    @PostMapping("/{orderId}/prepare")
    public ResponseEntity<CommonResponse<PaymentReadyResponse>> preparePayment(
            @PathVariable(name = "orderId") final Long productId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        PaymentReadyResponse paymentReadyResponse = paymentService.preparePayment(productId, principalDetails);
        return ResponseEntity.ok(CommonResponse.ok("Payment ready successfully", paymentReadyResponse));
    }

    @PostMapping("/result")
    public ResponseEntity<CommonResponse<PaymentResponse>> savePayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        PaymentResponse paymentResponse = paymentService.savePayment(request, principalDetails);
        return ResponseEntity.ok(CommonResponse.ok("Payment result", paymentResponse));
    }

    @PostMapping("/mobile/result")
    public ResponseEntity<CommonResponse<PaymentResponse>> saveMobliePayment(
            @RequestParam(name = "imp_uid") String impUid,
            @RequestParam(name = "merchant_uid") Long orderId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        PaymentRequest request = PaymentRequest.builder()
                .impUid(impUid)
                .orderId(orderId)
                .build();
        PaymentResponse paymentResponse = paymentService.savePayment(request, principalDetails);
        return ResponseEntity.ok(CommonResponse.ok("Payment result", paymentResponse));
    };
}
