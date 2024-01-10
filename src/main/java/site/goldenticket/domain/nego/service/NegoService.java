package site.goldenticket.domain.nego.service;

import site.goldenticket.domain.nego.dto.buyer.request.PricePurposeRequest;
import site.goldenticket.domain.nego.dto.buyer.response.ConfirmPriceResponse;
import site.goldenticket.domain.nego.dto.buyer.response.DenyPriceResponse;
import site.goldenticket.domain.nego.dto.buyer.response.PayResponse;
import site.goldenticket.domain.nego.dto.buyer.response.PricePurposeResponse;

public interface NegoService {

    // 판매자 입장
    ConfirmPriceResponse confirmPrice(Long negoId); // 가격승낙
    DenyPriceResponse denyPrice(Long negoId); // 거절하기

    // 구매자 입장
    PricePurposeResponse proposePrice(PricePurposeRequest request);
    PayResponse pay(Long negoId); // 결제하기
    //PayResponse payOriginPrice(Long negoId); // 원래가격으로결제
}
