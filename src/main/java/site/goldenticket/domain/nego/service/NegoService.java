package site.goldenticket.domain.nego.service;

import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.HandoverResponse;
import site.goldenticket.domain.nego.dto.response.NegoResponse;
import site.goldenticket.domain.nego.dto.response.PayResponse;
import site.goldenticket.domain.nego.dto.response.PriceProposeResponse;

public interface NegoService {

    // 판매자 입장
    NegoResponse confirmPrice(Long negoId); // 가격승낙
    NegoResponse denyPrice(Long negoId); // 거절하기

    // 구매자 입장
    PriceProposeResponse proposePrice(Long productId, PriceProposeRequest request);
    PayResponse pay(Long negoId); // 결제하기

    PayResponse payOriginPrice(Long negoId); //원래 가격으로 결제하기

    HandoverResponse handOverProduct(Long negoId);
}
