package site.goldenticket.domain.nego.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.nego.dto.buyer.request.PricePurposeRequest;
import site.goldenticket.domain.nego.dto.buyer.response.PricePurposeResponse;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NegoServiceImpl implements NegoService{

    private NegoRepository negoRepository;


    @Override
    public void confirmPrice(Long negoId) {
        // 네고 아이디로 네고 조회
        Optional<Nego> optionalNego = negoRepository.findById(negoId);

        if (optionalNego.isPresent()) {
            Nego nego = optionalNego.get();

            // 현재 상태가 NEGOTIATING 또는 PAYMENT_PENDING인 경우에만 처리
            if (nego.getStatus() == NegotiationStatus.NEGOTIATING) {
                // 가격 승낙 처리
                nego.setStatus(NegotiationStatus.PAYMENT_PENDING);

                // 네고 엔티티 저장
                negoRepository.save(nego);
            } else {
                // 다른 상태의 네고는 가격 승낙을 처리할 수 없음
                throw new CustomException(ErrorCode.COMMON_INVALID_PARAMETER);
            }
        } else {
            // 해당 네고 아이디가 존재하지 않음
            throw new NoSuchElementException("Nego not found with id: " + negoId);
        }
    }

    @Override
    public void denyPrice() {

    }

    @Override
    public void modifyPrice() {

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
    private void updateCountForNewNego(Nego nego) {
        // Increment count
        nego.setCount((nego.getCount() != null ? nego.getCount() : 0) + 1);
        // Save the updated entity
        negoRepository.save(nego);
    }


    @Override
    public void pay() {

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
