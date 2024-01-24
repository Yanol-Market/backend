package site.goldenticket.domain.nego.service;

import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.*;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.security.PrincipalDetails;

import java.util.List;

public interface NegoService {

    NegoResponse confirmPrice(Long negoId, PrincipalDetails principalDetails);

    NegoResponse denyPrice(Long negoId, PrincipalDetails principalDetails);

    PriceProposeResponse proposePrice(Long productId, PriceProposeRequest request, PrincipalDetails principalDetails);

    PayResponse pay(Long negoId, PrincipalDetails principalDetails);

    HandoverResponse handOverProduct(Long negoId, PrincipalDetails principalDetails);

    NegoResponse denyHandoverProduct(Long negoId, PrincipalDetails principalDetails);

    List<Nego> getUserNego(Long userId);

    Nego save(Nego nego);

    NegoAvailableResponse isAvailableNego(Long userId, Long productId); //네고 가능 여부 조회

    NegoTestListResponse getNegoListForTest(); // 테스트용 모든 네고 기록 조회
    List<Nego> findByStatusInAndProduct(List<NegotiationStatus> negotiationStatusList, Product product);
}
