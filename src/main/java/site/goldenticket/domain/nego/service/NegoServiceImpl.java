package site.goldenticket.domain.nego.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.NegoResponse;
import site.goldenticket.domain.nego.dto.response.PayResponse;
import site.goldenticket.domain.nego.dto.response.PriceProposeResponse;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NegoServiceImpl implements NegoService {

    private final NegoRepository negoRepository;

    @Override
    public NegoResponse confirmPrice(Long negoId) {
        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + negoId));

        if (nego.getStatus() == NegotiationStatus.NEGOTIATING) {
            nego.setUpdatedAt(LocalDateTime.now());
            nego.setStatus(NegotiationStatus.PENDING);
            nego.setExpirationTime(LocalDateTime.now().plusMinutes(20));
            nego.setConsent(Boolean.TRUE);
            negoRepository.save(nego);

        } else {
            // 다른 상태의 네고는 가격 승낙을 처리할 수 없음
            throw new CustomException("네고를 승인할수 없습니다.", ErrorCode.COMMONT_CANNOT_CONFIRM_NEGO);
        }
        if (nego.getCount() == 2) {
            throw new CustomException("네고를 승인할수 없습니다.", ErrorCode.COMMONT_CANNOT_CONFIRM_NEGO);
        }
        return NegoResponse.fromEntity(nego);
    }


    @Override
    public NegoResponse denyPrice(Long negoId) {
        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        if (nego.getStatus() == NegotiationStatus.NEGOTIATING || nego.getStatus() == NegotiationStatus.NEGOTIATION_CANCELLED) {
            nego.setUpdatedAt(LocalDateTime.now());
            nego.setConsent(Boolean.FALSE);
            nego.setUpdatedAt(LocalDateTime.now());

            // 네고 취소 상태로 변경
            if (nego.getCount() == 2) {
                nego.setStatus(NegotiationStatus.NEGOTIATION_CANCELLED);
            }

            negoRepository.save(nego);  // 네고 업데이트
            return NegoResponse.fromEntity(nego);
        } else {
            // NEGOTIATING 상태가 아닌 경우 거절 처리 불가
            throw new CustomException("네고 중인 경우에만 거절할 수 있습니다.", ErrorCode.COMMONT_ONLY_CAN_DENY_WHEN_NEGOTIATING);
        }
    }


    // 가격제안은 productId를 받아서 사용할 예정 아래는 임시!
    @Override
    public PriceProposeResponse proposePrice(PriceProposeRequest request) {
        Nego nego = request.toEntity();
        updateCountForNewNego(nego);
        nego.setUpdatedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(nego.getConsent())) {
            throw new CustomException("승인된 네고는 가격 제안을 할 수 없습니다.", ErrorCode.COMMON_NEGO_ALREADY_APPROVED);
        }

        // 승낙 여부가 null인 경우 false로 설정
        if (nego.getConsent() == null) {
            nego.setConsent(Boolean.FALSE);
        }

        if (Boolean.FALSE.equals(nego.getConsent())) {
            nego.setStatus(NegotiationStatus.NEGOTIATING);

            if (nego.getCount() == 3) {
                // 3번째 가격 제안이면 NEGO 취소로 상태 변경
                nego.setStatus(NegotiationStatus.NEGOTIATION_CANCELLED);
                negoRepository.save(nego);
                return PriceProposeResponse.fromEntity(nego); // NEGO 취소 상태로 변경하고 응답
            }

            negoRepository.save(nego);
        } else {
            throw new CustomException("네고를 제안할 수 없는 상태입니다.", ErrorCode.COMMON_CANNOT_NEGOTIATE);
        }

        return PriceProposeResponse.fromEntity(nego);
    }


    @Override
    public PayResponse pay(Long negoId) {
        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        if (nego.getConsent()) {
            nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
            nego.setUpdatedAt(LocalDateTime.now());
            negoRepository.save(nego);

            return PayResponse.fromEntity(nego);
        } else {
            throw new CustomException("네고 승인이 필요합니다.", ErrorCode.COMMON_NEGO_APPROVAL_REQUIRED);
        }
    }

    private void updateCountForNewNego(Nego newNego) {
        // productId와 userId에 해당하는 네고 중 가장 최근의 것을 가져옴
        Optional<Nego> latestNegoOptional = negoRepository.findLatestNegoByProductIdAndUserIdOrderByCreatedAtDesc(newNego.getProductId(), newNego.getUserId(), PageRequest.of(0, 1))
                .stream()
                .findFirst();

        // 최근의 네고가 있으면 count를 1 증가, 없으면 1로 초기화
        int newCount = latestNegoOptional.map(latestNego -> latestNego.getCount() + 1).orElse(1);

        // 여기서 count가 3인 경우 예외 처리
        if (newCount > 2) {
            throw new CustomException("더 이상 네고할 수 없습니다.", ErrorCode.COMMON_CANNOT_NEGOTIATE);
        }

        newNego.setCount(newCount);
    }


//    @Override
//    public PayResponse payOriginPrice(Long negoId) {
//        Nego nego = negoRepository.findById(negoId)
//                .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));
//
//        if (nego.getConsent()) {
//            Integer originPrice = nego.getProduct().getOriginPrice();
//
//
//            // 네고의 가격을 상품의 원래 가격으로 업데이트
//            nego.setPrice(originPrice);
//
//            // 네고 상태를 완료로 변경
//            nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
//            nego.setUpdatedAt(LocalDateTime.now());
//            negoRepository.save(nego);
//
//            return PayResponse.fromEntity(nego);
//        } else {
//            throw new CustomException("네고 승인이 필요합니다.", ErrorCode.COMMON_INVALID_PARAMETER);
//        }
//    }

}
