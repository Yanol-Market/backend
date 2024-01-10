package site.goldenticket.domain.nego.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.nego.dto.buyer.request.PricePurposeRequest;
import site.goldenticket.domain.nego.dto.buyer.response.ConfirmPriceResponse;
import site.goldenticket.domain.nego.dto.buyer.response.DenyPriceResponse;
import site.goldenticket.domain.nego.dto.buyer.response.PayResponse;
import site.goldenticket.domain.nego.dto.buyer.response.PricePurposeResponse;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class NegoServiceImpl implements NegoService {

    private final NegoRepository negoRepository;


    @Override
    public ConfirmPriceResponse confirmPrice(Long negoId) {
        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + negoId));

        // 현재 상태가 NEGOTIATING 또는 PAYMENT_PENDING인 경우에만 처리
        if (nego.getStatus() == NegotiationStatus.NEGOTIATING) {
            nego.setStatus(NegotiationStatus.PENDING);
            nego.setExpirationTime(LocalDateTime.now().plusMinutes(20));
            nego.setConsent(Boolean.TRUE);
            negoRepository.save(nego);  // 네고 업데이트

        } else {
            // 다른 상태의 네고는 가격 승낙을 처리할 수 없음
            throw new CustomException(ErrorCode.COMMON_INVALID_PARAMETER);
        }
        return ConfirmPriceResponse.fromEntity(nego);
    }


    @Override
    public DenyPriceResponse denyPrice(Long negoId) {
        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + negoId));

        if (nego.getCount() == 1) {
            nego.setConsent(Boolean.FALSE);
            negoRepository.save(nego);  // 네고 업데이트
            return null;
        } else if (nego.getCount() == 2) {
            // count가 2인 경우, 가격 거절 처리
            nego.setConsent(Boolean.FALSE);
            nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
            negoRepository.save(nego);  // 네고 업데이트
        }
        return DenyPriceResponse.fromEntity(nego);
    }


    @Override
    public PricePurposeResponse proposePrice(PricePurposeRequest request) {

        Nego nego = request.toEntity();
        updateCountForNewNego(nego);
        nego.setStatus(NegotiationStatus.NEGOTIATING);

        // 네고 엔티티 저장
        negoRepository.save(nego);

        // 저장된 네고 엔티티를 응답 DTO로 변환
        return PricePurposeResponse.fromEntity(nego);
    }

    @Override
    public PayResponse pay(Long negoId) {
        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("Negotiation not found with id: " + negoId));

        nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
        nego.setUpdatedAt(LocalDateTime.now()); // 이 부분 추가
        negoRepository.save(nego);

        return PayResponse.fromEntity(nego);
    }

    private void updateCountForNewNego(Nego nego) {
        // Increment count
        nego.setCount((nego.getCount() != null ? nego.getCount() : 0) + 1);
        // Save the updated entity
        negoRepository.save(nego);
    }


    @Override
    public void payOriginPrice() {

    }

    static class Product {
        private Long productId = 1L;
        private Long userId = 101L;
        private String imageUrl = "default-image-url.jpg";
        private String accommodationName = "Default Accommodation";
        private String roomName = "Default Room";
        private String reservationType = "숙박";
        private Integer standardNumber = 1;
        private Integer maximumNumber = 2;
        private Integer goldenPrice = 100;
        private LocalDate checkInDate = LocalDate.now();
        private LocalTime checkInTime = LocalTime.now();
        private LocalDate checkOutDate = LocalDate.now().plusDays(1);
        private LocalTime checkOutTime = LocalTime.now().plusHours(1);
        private String status = "판매중";

        public boolean isOnSale() {
            return this.status.equals("판매중");
        }

        public boolean isNotOnSale() {
            return !isOnSale();
        }
    }


    static class User {
        private Long id = 1L;
        private String name = "test";
        private String phoneNumber = "010-1234-5678";
        private String email = "test@mail";
    }
}
