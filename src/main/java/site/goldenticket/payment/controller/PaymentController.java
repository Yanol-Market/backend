package site.goldenticket.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.payment.dto.response.PaymentDetailResponse;
import site.goldenticket.payment.service.PaymentService;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{productId}")
    public CommonResponse<PaymentDetailResponse> getPaymentDetail(@PathVariable("productId") final Long productId) {
        PaymentDetailResponse paymentDetail = paymentService.getPaymentDetail(productId);
        return CommonResponse.ok("Payment details retrieved successfully", paymentDetail);
    }
}
