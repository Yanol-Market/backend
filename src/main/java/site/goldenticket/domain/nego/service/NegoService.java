package site.goldenticket.domain.nego.service;

import site.goldenticket.domain.nego.dto.buyer.request.PricePurposeRequest;
import site.goldenticket.domain.nego.dto.buyer.response.PricePurposeResponse;

public interface NegoService {

    // 판매자 입장
    void confirmPrice(Long negoId); // 가격승낙
    void denyPrice(); // 거절하기
    void modifyPrice(); // 가격수정


    // 구매자 입장
    PricePurposeResponse proposePrice(PricePurposeRequest request);
    void pay(); // 결제하기
    void payOriginPrice(); // 원라가격으로결제
}
