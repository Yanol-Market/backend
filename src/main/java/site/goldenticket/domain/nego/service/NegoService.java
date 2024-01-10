package site.goldenticket.domain.nego.service;

import site.goldenticket.domain.nego.dto.buyer.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.buyer.response.PriceResponse;
import site.goldenticket.domain.nego.dto.buyer.response.PayResponse;
import site.goldenticket.domain.nego.dto.buyer.response.PriceProposeResponse;

public interface NegoService {

    // 판매자 입장
    PriceResponse confirmPrice(Long negoId); // 가격승낙
    PriceResponse denyPrice(Long negoId); // 거절하기

    // 구매자 입장
    PriceProposeResponse proposePrice(PriceProposeRequest request);
    PayResponse pay(Long negoId); // 결제하기
    //PayResponse payOriginPrice(Long negoId); // 원래가격으로결제
}
