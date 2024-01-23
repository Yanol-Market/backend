package site.goldenticket.domain.nego.service;

import static site.goldenticket.common.response.ErrorCode.USER_NOT_FOUND;
import static site.goldenticket.domain.nego.status.NegotiationStatus.NEGOTIATION_COMPLETED;
import static site.goldenticket.domain.nego.status.NegotiationStatus.NEGOTIATION_TIMEOUT;
import static site.goldenticket.domain.nego.status.NegotiationStatus.PAYMENT_PENDING;
import static site.goldenticket.domain.nego.status.NegotiationStatus.TRANSFER_PENDING;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.alert.service.AlertService;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NegoSchedulerService {

    private final NegoRepository negoRepository;
    private final ProductService productService;
    private final AlertService alertService;
    private final UserRepository userRepository;

    @Scheduled(fixedDelay = 60000)
    public void changeStatus() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Nego> pendingNegos = negoRepository.findByStatus(PAYMENT_PENDING);
        List<Nego> transferNegos = negoRepository.findByStatus(TRANSFER_PENDING);

        for (Nego nego : pendingNegos) {
            Product product = productService.getProduct(nego.getProductId());
            LocalDateTime updatedAt = nego.getUpdatedAt();
            if (updatedAt != null && currentTime.isAfter(updatedAt.plusMinutes(20))) {
                product.setProductStatus(ProductStatus.SELLING);
                productService.updateProductForNego(product);
                nego.setStatus(NEGOTIATION_TIMEOUT);
                nego.setUpdatedAt(currentTime);

                //판매자에게 타임오버 알림 전송
                alertService.createAlert(product.getUserId(),
                    "구매자가 20분 이내에 결제를 완료하지 않아 거래가 이루어지지 않았습니다.");
                //구매자에게 타임오버 알림 전송
                alertService.createAlert(nego.getUser().getId(),
                    "20분이 초과되었습니다. 아직 구매를 원하신다면, 재결제 버튼을 눌러 결제해주세요.");
            }
        } //상품 상태 판매중

        for (Nego transferNego : transferNegos) {
            Product product = productService.getProduct(transferNego.getProductId());
            LocalDateTime updatedAt = transferNego.getUpdatedAt();
            if (updatedAt != null && currentTime.isAfter(updatedAt.plusMinutes(20))) {
                transferNego.setStatus(NEGOTIATION_COMPLETED);
                transferNego.setUpdatedAt(currentTime);
                product.setProductStatus(ProductStatus.SOLD_OUT);
                productService.updateProductForNego(product);

                //구매자에게 양도 완료 알림 전송
                alertService.createAlert(transferNego.getUser().getId(),
                    "'" + product.getAccommodationName() + "(" + product.getRoomName()
                        + ")'상품 양도가 완료되었습니다. "
                        + "양도 완료에 따른 체크인 정보는 '마이페이지 > 구매내역 > 구매 완료'에서 확인하실 수 있습니다.");
                //판매자에게 정산 요청 알림 전송
                alertService.createAlert(product.getUserId(),
                    "'" + product.getAccommodationName() + "(" + product.getRoomName()
                        + ")'상품 양도가 완료되었습니다. 영업일 1일 이내 등록한 계좌 정보로 정산 금액이 입금됩니다."
                        + "원활한 정산 진행을 위해 '마이페이지 - 나의 계좌'정보를 다시 한번 확인해주세요.");
                //판매자에게 계좌 등록 알림 전송
                User user = userRepository.findById(product.getUserId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
                if (user.getAccountNumber().equals(null)) {
                    alertService.createAlert(product.getUserId(),
                        "'" + product.getAccommodationName() + "(" + product.getRoomName()
                            + ")'상품에 대한 원활한 정산을 위해 '마이페이지 > 내 계좌'에서 입금받으실 계좌를 등록해주세요.");
                }
            }
        }   // 3시간 뒤 자동양도

        negoRepository.saveAll(transferNegos);
        negoRepository.saveAll(pendingNegos);
    }

}
