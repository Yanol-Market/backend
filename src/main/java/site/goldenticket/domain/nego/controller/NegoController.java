package site.goldenticket.domain.nego.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.nego.dto.buyer.request.PricePurposeRequest;
import site.goldenticket.domain.nego.dto.buyer.response.ConfirmPriceResponse;
import site.goldenticket.domain.nego.dto.buyer.response.DenyPriceResponse;
import site.goldenticket.domain.nego.dto.buyer.response.PayResponse;
import site.goldenticket.domain.nego.dto.buyer.response.PricePurposeResponse;
import site.goldenticket.domain.nego.service.NegoService;

@RestController
@RequestMapping("/nego")
@RequiredArgsConstructor

public class NegoController {
    private final NegoService negoService;

    @PostMapping("/proposePrice")
    public CommonResponse<PricePurposeResponse> proposePrice(@RequestBody PricePurposeRequest request) {
        PricePurposeResponse response = negoService.proposePrice(request);
        return CommonResponse.ok("네고가 전달되었습니다.", response);
    }

    @PostMapping("/confirm/{negoId}")
    public CommonResponse<ConfirmPriceResponse> confirmPrice(@PathVariable Long negoId) {
        String message = "Price confirmation successful";
        negoService.confirmPrice(negoId);
        return CommonResponse.ok("네고가 승인되었습니다",new ConfirmPriceResponse(message));
    }

    @PostMapping("/deny/{negoId}")
    public CommonResponse<DenyPriceResponse> denyPrice(@PathVariable Long negoId){
        String message = "Negotiation has denied";
        negoService.denyPrice(negoId);
        return CommonResponse.ok("네고가 거절되었습니다", new DenyPriceResponse(message));
    }

    @PostMapping("/pay/{negoId}")
    public CommonResponse<PayResponse> pay(@PathVariable Long negoId) {
        PayResponse payResponse = negoService.pay(negoId);
        return CommonResponse.ok("결제가 진행됩니다", payResponse);
    }
}
