package site.goldenticket.domain.nego.service;

import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.*;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.security.PrincipalDetails;

import java.util.Optional;

public interface NegoService {

    // 판매자 입장
    NegoResponse confirmPrice(Long negoId, PrincipalDetails principalDetails); // 가격승낙

    NegoResponse denyPrice(Long negoId, PrincipalDetails principalDetails); // 거절하기

    // 구매자 입장
    PriceProposeResponse proposePrice(Long productId, PriceProposeRequest request, PrincipalDetails principalDetails);

    PayResponse pay(Long negoId, PrincipalDetails principalDetails); // 결제하기

    HandoverResponse handOverProduct(Long negoId, PrincipalDetails principalDetails);

    NegoResponse denyHandoverProduct(Long negoId, PrincipalDetails principalDetails);

    Optional<Nego> getNego(Long userId, Long productId);

    Nego save(Nego nego);

    NegoAvailableResponse isAvailableNego(Long userId, Long productId); //네고 가능 여부 조회
}
