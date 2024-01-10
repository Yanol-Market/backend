package site.goldenticket.domain.nego.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.nego.dto.buyer.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.buyer.response.PriceResponse;
import site.goldenticket.domain.nego.dto.buyer.response.PayResponse;
import site.goldenticket.domain.nego.dto.buyer.response.PriceProposeResponse;
import site.goldenticket.domain.nego.service.NegoService;

@RestController
@RequestMapping("/nego")
@RequiredArgsConstructor

public class NegoController {
    private final NegoService negoService;

    @PostMapping("/proposePrice")
    public CommonResponse<PriceProposeResponse> proposePrice(@RequestBody PriceProposeRequest request) {
        PriceProposeResponse response = negoService.proposePrice(request);
        return CommonResponse.ok("네고가 전달되었습니다.", response);
    }

    @PatchMapping("/confirm/{negoId}")
    public CommonResponse<PriceResponse> confirmPrice(@PathVariable Long negoId) {
        PriceResponse response = negoService.confirmPrice(negoId);
        return CommonResponse.ok("네고가 승인되었습니다", response);
    }

    @PatchMapping("/deny/{negoId}")
    public CommonResponse<PriceResponse> denyPrice(@PathVariable Long negoId){
        PriceResponse response = negoService.denyPrice(negoId);
        return CommonResponse.ok("네고가 거절되었습니다", response);
    }

    @PatchMapping("/pay/{negoId}")
    public CommonResponse<PayResponse> pay(@PathVariable Long negoId) {
        PayResponse payResponse = negoService.pay(negoId);
        return CommonResponse.ok("결제가 진행됩니다", payResponse);
    }
 /*   @PostMapping("/payOriginPrice/{negoId}")
    public CommonResponse<PayResponse> payOriginPrice(@PathVariable Long negoId) {
        PayResponse payResponse = negoService.payOriginPrice(negoId);
        return CommonResponse.ok("결제가 완료되었습니다", payResponse);
    }*/
}
