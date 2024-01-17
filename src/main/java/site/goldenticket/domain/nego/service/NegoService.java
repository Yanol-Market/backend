package site.goldenticket.domain.nego.service;

import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.HandoverResponse;
import site.goldenticket.domain.nego.dto.response.NegoAvailableResponse;
import site.goldenticket.domain.nego.dto.response.NegoResponse;
import site.goldenticket.domain.nego.dto.response.PayResponse;
import site.goldenticket.domain.nego.dto.response.PriceProposeResponse;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.security.PrincipalDetails;

import java.util.Optional;

public interface NegoService {

    // 판매자 입장
    NegoResponse confirmPrice(Long negoId,PrincipalDetails principalDetails); // 가격승낙
    NegoResponse denyPrice(Long negoId,PrincipalDetails principalDetails); // 거절하기

    // 구매자 입장
    PriceProposeResponse proposePrice(Long productId, PriceProposeRequest request, PrincipalDetails principalDetails);
    PayResponse pay(Long negoId,PrincipalDetails principalDetails); // 결제하기

    PayResponse payOriginPrice(Long negoId,PrincipalDetails principalDetails); //원래 가격으로 결제하기

    HandoverResponse handOverProduct(Long negoId, PrincipalDetails principalDetails);


    Optional<Nego> getNego(Long userId, Long productId);

    Optional<Nego> getUserNego(Long userId);

    Nego save(Nego nego);

    NegoAvailableResponse isAvailableNego(Long userId, Long productId); //네고 가능 여부 조회
}
