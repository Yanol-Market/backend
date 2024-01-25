//package site.goldenticket.domain.nego.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import site.goldenticket.common.exception.CustomException;
//import site.goldenticket.common.response.ErrorCode;
//import site.goldenticket.domain.alert.service.AlertService;
//import site.goldenticket.domain.nego.entity.Nego;
//import site.goldenticket.domain.nego.repository.NegoRepository;
//import site.goldenticket.domain.payment.model.Order;
//import site.goldenticket.domain.payment.repository.OrderRepository;
//import site.goldenticket.domain.product.constants.ProductStatus;
//import site.goldenticket.domain.product.model.Product;
//import site.goldenticket.domain.product.service.ProductService;
//import site.goldenticket.domain.user.entity.User;
//import site.goldenticket.domain.user.repository.UserRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static site.goldenticket.common.constants.OrderStatus.COMPLETED_TRANSFER;
//import static site.goldenticket.common.constants.OrderStatus.WAITING_TRANSFER;
//import static site.goldenticket.common.response.ErrorCode.USER_NOT_FOUND;
//import static site.goldenticket.domain.nego.status.NegotiationStatus.*;
//
//@Service
//@RequiredArgsConstructor
//public class NegoSchedulerService {
//
//    private final NegoRepository negoRepository;
//    private final ProductService productService;
//    private final AlertService alertService;
//    private final UserRepository userRepository;
//    private final OrderRepository orderRepository;
//
//    @Scheduled(fixedDelay = 1000)
//    public void changeStatus() {
//        LocalDateTime currentTime = LocalDateTime.now();
//        List<Nego> pendingNegos = negoRepository.findByStatus(PAYMENT_PENDING);
//        List<Order> transferOrders = orderRepository.findByStatus(WAITING_TRANSFER);
//
//        for (Nego nego : pendingNegos) {
//            Product product = productService.getProduct(nego.getProductId());
//            LocalDateTime updatedAt = nego.getUpdatedAt();
//            if (updatedAt != null && currentTime.isAfter(updatedAt.plusMinutes(5))) {
//                product.setProductStatus(ProductStatus.SELLING);
//                productService.updateProductForNego(product);
//                nego.setStatus(NEGOTIATION_TIMEOUT);
//                nego.setUpdatedAt(currentTime);
//
//                negoRepository.save(nego);
//
//                //판매자에게 타임오버 알림 전송
//                alertService.createAlert(product.getUserId(),
//                    "구매자가 20분 이내에 결제를 완료하지 않아 거래가 이루어지지 않았습니다.");
//                //구매자에게 타임오버 알림 전송
//                alertService.createAlert(nego.getUser().getId(),
//                    "20분이 초과되었습니다. 아직 구매를 원하신다면, 재결제 버튼을 눌러 결제해주세요.");
//                //해당 상품 찜한 회원들에게 알림 전송
//                if (product.getProductStatus().equals(ProductStatus.SELLING)) {
//                    alertService.createAlertOfWishProductToSelling(product.getId(),
//                        product.getAccommodationName(), product.getRoomName());
//                }
//            }
//        } //상품 상태 판매중
//
//        for (Order transferOrder : transferOrders) {
//            Product product = productService.getProduct(transferOrder.getProductId());
//            List<Nego> transferNegos = negoRepository.findAllByProduct(product);
//            LocalDateTime updatedAt = transferOrder.getUpdatedAt();
//
//            User user = userRepository.findById(product.getUserId())
//                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
//
//            checkAccountAndThrowException(user);
//
//            if (updatedAt != null && currentTime.isAfter(updatedAt.plusMinutes(5))) {
//                for (Nego transferNego : transferNegos) {
//                    // 각 네고의 현재 상태를 확인하고 처리
//                    if (transferNego.getStatus() == NEGOTIATION_COMPLETED) {
//                        continue;
//                    }
//                    transferOrder.setStatus(COMPLETED_TRANSFER);
//                    transferNego.setStatus(NEGOTIATION_COMPLETED);
//                    transferNego.setUpdatedAt(currentTime);
//
//                    // 상품의 상태를 변경하고 업데이트
//                    product.setProductStatus(ProductStatus.SOLD_OUT);
//                    productService.updateProductForNego(product);
//
//                    // 구매자에게 양도 완료 알림 전송
//                    alertService.createAlert(transferNego.getUser().getId(),
//                        "'" + product.getAccommodationName() + "(" + product.getRoomName()
//                            + ")'상품 양도가 완료되었습니다. "
//                            + "양도 완료에 따른 체크인 정보는 '마이페이지 > 구매내역 > 구매 완료'에서 확인하실 수 있습니다.");
//
//                    // 판매자에게 정산 요청 알림 전송
//                    alertService.createAlert(product.getUserId(),
//                        "'" + product.getAccommodationName() + "(" + product.getRoomName()
//                            + ")'상품 양도가 완료되었습니다. 영업일 1일 이내 등록한 계좌 정보로 정산 금액이 입금됩니다."
//                            + "원활한 정산 진행을 위해 '마이페이지 - 나의 계좌'정보를 다시 한번 확인해주세요.");
//
//                    // 판매자에게 계좌 등록 알림 전송
//
//                    if (user != null && user.getAccountNumber() == null) {
//                        alertService.createAlert(product.getUserId(),
//                            "'" + product.getAccommodationName() + "(" + product.getRoomName()
//                                + ")'상품에 대한 원활한 정산을 위해 '마이페이지 > 내 계좌'에서 입금받으실 계좌를 등록해주세요.");
//                    }
//                }
//                negoRepository.saveAll(transferNegos);
//            }
//        } // 20분 뒤 자동양도
//    }
//
//    private void checkAccountAndThrowException(User user) {
//        if (user.getAccountNumber() == null) {
//            throw new CustomException("등록된 계좌가 없습니다.", ErrorCode.NO_REGISTERED_ACCOUNT);
//        }
//    }
//}
