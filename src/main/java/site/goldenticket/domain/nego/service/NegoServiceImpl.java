package site.goldenticket.domain.nego.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.HandoverResponse;
import site.goldenticket.domain.nego.dto.response.NegoResponse;
import site.goldenticket.domain.nego.dto.response.PayResponse;
import site.goldenticket.domain.nego.dto.response.PriceProposeResponse;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NegoServiceImpl implements NegoService {

    private final NegoRepository negoRepository;
    private final ProductService productService;

    @Override
    public NegoResponse confirmPrice(Long negoId) {
        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + negoId));

        if (nego.getStatus() == NegotiationStatus.NEGOTIATING) {
            nego.setUpdatedAt(LocalDateTime.now());
            nego.setStatus(NegotiationStatus.PAYMENT_PENDING);
            nego.setExpirationTime(LocalDateTime.now().plusMinutes(20));
            nego.setConsent(Boolean.TRUE);
            negoRepository.save(nego);

        } else {
            // 다른 상태의 네고는 가격 승낙을 처리할 수 없음
            throw new CustomException("네고를 승인할수 없습니다.", ErrorCode.COMMON_CANNOT_CONFIRM_NEGO);
        }
        if (nego.getCount() == 2) {
            throw new CustomException("네고를 승인할수 없습니다.", ErrorCode.COMMON_CANNOT_CONFIRM_NEGO);
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
            throw new CustomException("네고 중인 경우에만 거절할 수 있습니다.", ErrorCode.COMMON_ONLY_CAN_DENY_WHEN_NEGOTIATING);
        }
    }


    // 가격제안은 productId를 받아서 사용할 예정 아래는 임시!
    @Override
    public PriceProposeResponse proposePrice(Long productId, PriceProposeRequest request) {
        // productId 활용 추가
        Nego nego = request.toEntity();
        updateCountForNewNego(nego);
        nego.setUpdatedAt(LocalDateTime.now());

        // 네고 상태가 TIMEOUT이면 가격 제안 불가능 예외 처리
        if (NegotiationStatus.NEGOTIATION_TIMEOUT.equals(nego.getStatus())) {
            throw new CustomException("20분이 지나 가격 제안을 할 수 없습니다.", ErrorCode.COMMON_NEGO_TIMEOUT);
        }

        if (Boolean.TRUE.equals(nego.getConsent())) {
            throw new CustomException("승인된 네고는 가격 제안을 할 수 없습니다.", ErrorCode.COMMON_NEGO_ALREADY_APPROVED);
        }

        // 네고를 한 사용자의 ID로 네고를 찾음
        Optional<Nego> userNegoOptional = negoRepository.findLatestNegoByUserIdAndProductIdOrderByCreatedAtDesc(
                        nego.getUserId(), productId, PageRequest.of(0, 1))
                .stream()
                .findFirst();

        userNegoOptional.ifPresent(userNego -> {
            if (NegotiationStatus.NEGOTIATION_TIMEOUT.equals(userNego.getStatus())) {
                throw new CustomException("이미 타임아웃된 네고가 있어 가격 제안을 할 수 없습니다.", ErrorCode.COMMON_NEGO_TIMEOUT);
            }

            if (Boolean.TRUE.equals(userNego.getConsent()) && NegotiationStatus.PAYMENT_PENDING.equals(userNego.getStatus())) {
                throw new CustomException("이미 승인된 네고가 있어 가격 제안을 할 수 없습니다.", ErrorCode.COMMON_NEGO_ALREADY_APPROVED);
            }
        });

        if (nego.getConsent() == null) {
            nego.setConsent(Boolean.FALSE);
        }

        if (Boolean.FALSE.equals(nego.getConsent()) || NegotiationStatus.NEGOTIATING.equals(nego.getStatus())) {
            nego.setStatus(NegotiationStatus.NEGOTIATING);

            if (nego.getCount() == 3) {
                nego.setStatus(NegotiationStatus.NEGOTIATION_CANCELLED);
                negoRepository.save(nego);
                return PriceProposeResponse.fromEntity(nego);
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


    @Override
    public PayResponse payOriginPrice(Long negoId) {
        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        if (nego.getConsent()) {
            Integer originPrice = nego.getProduct().getOriginPrice();


            // 네고의 가격을 상품의 원래 가격으로 업데이트
            nego.setPrice(originPrice);

            // 네고 상태를 완료로 변경
            nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
            nego.setUpdatedAt(LocalDateTime.now());
            negoRepository.save(nego);

            return PayResponse.fromEntity(nego);
        } else {
            throw new CustomException("네고 승인이 필요합니다.", ErrorCode.COMMON_INVALID_PARAMETER);
        }
    }

    @Override
    public HandoverResponse handOverProduct(Long negoId) {
        // Nego ID로 Nego 정보 가져오기
        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        // Nego의 Product ID로 Product 정보 가져오기
        Product product = productService.findProductById(nego.getProductId());

        // 상태가 결제 완료인 경우에만 양도 가능
        if (nego.getStatus() == NegotiationStatus.NEGOTIATION_COMPLETED) {
            // 양도를 위한 작업 수행 (예: 상품 소유자 변경 등)

            // HandoverResponse 생성
            HandoverResponse handoverResponse = HandoverResponse.fromEntity(product, nego);

            // 양도 작업이 완료된 경우에는 양도 정보와 함께 반환
            return handoverResponse;
        } else {
            // 양도 불가능한 상태인 경우 예외 처리
            throw new CustomException("양도가 불가능한 상태입니다.", ErrorCode.COMMON_CANNOT_HANDOVER);
        }
    }
}
