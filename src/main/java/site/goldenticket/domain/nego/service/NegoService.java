package site.goldenticket.domain.nego.service;

import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.*;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.security.PrincipalDetails;

import java.util.List;
import java.util.Optional;

public interface NegoService {

    NegoResponse confirmPrice(Long negoId, PrincipalDetails principalDetails);

    NegoResponse denyPrice(Long negoId, PrincipalDetails principalDetails);

    PriceProposeResponse proposePrice(Long productId, PriceProposeRequest request, PrincipalDetails principalDetails);

    PayResponse pay(Long negoId, PrincipalDetails principalDetails);

    HandoverResponse handOverProduct(Long negoId, PrincipalDetails principalDetails);

    NegoResponse denyHandoverProduct(Long negoId, PrincipalDetails principalDetails);

    Optional<Nego> getNego(Long userId, Long productId);

    Nego save(Nego nego);

    NegoAvailableResponse isAvailableNego(Long userId, Long productId); //네고 가능 여부 조회

    List<Nego> findByStatusInAndProductId(List<NegotiationStatus> negotiationStatusList, Long productId);
}
