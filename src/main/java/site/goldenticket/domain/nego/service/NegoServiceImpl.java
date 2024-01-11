package site.goldenticket.domain.nego.service;

import lombok.RequiredArgsConstructor;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class NegoServiceImpl implements NegoService {

    private final NegoRepository negoRepository;
    private final SchedulerService schedulerService;

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
            throw new CustomException(ErrorCode.COMMON_INVALID_PARAMETER);
        }
        return NegoResponse.fromEntity(nego);
    }


    @Override
    public NegoResponse denyPrice(Long negoId) {
        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        // status가 NEGOTIATING일 때만 처리
        if (nego.getStatus() == NegotiationStatus.NEGOTIATING) {
            nego.setUpdatedAt(LocalDateTime.now());
            nego.setConsent(Boolean.FALSE);
            nego.setUpdatedAt(LocalDateTime.now());
            negoRepository.save(nego);  // 네고 업데이트
            return NegoResponse.fromEntity(nego);
        } else {
            // NEGOTIATING 상태가 아닌 경우 거절 처리 불가
            throw new CustomException("네고 중인 경우에만 거절할 수 있습니다.", ErrorCode.COMMON_INVALID_PARAMETER);
        }
    }




    @Override
    public PriceProposeResponse proposePrice(PriceProposeRequest request) {

        Nego nego = request.toEntity();
        updateCountForNewNego(nego);
        nego.setUpdatedAt(LocalDateTime.now());
        nego.setConsent(Boolean.FALSE);
        nego.setStatus(NegotiationStatus.NEGOTIATING);
        if (nego.getCount() == 3) {
            nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
            throw new CustomException("더 이상 네고할 수 없습니다.", ErrorCode.COMMON_INVALID_PARAMETER);
        }
        negoRepository.save(nego);

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
            throw new CustomException("네고 승인이 필요합니다.", ErrorCode.COMMON_INVALID_PARAMETER);
        }
    }

    private void updateCountForNewNego(Nego newNego) {
        // productId와 userId에 해당하는 네고 중 가장 최근의 것을 가져옴
        Nego latestNego = negoRepository.findLatestNegoByProductIdAndUserIdOrderByCreatedAtDesc(newNego.getProductId(), newNego.getUserId());
        // 최근의 네고가 있으면 count를 1 증가, 없으면 1로 초기화
        newNego.setCount(latestNego != null ? latestNego.getCount() + 1 : 1);
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
