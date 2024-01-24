package site.goldenticket.domain.nego.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.alert.service.AlertService;
import site.goldenticket.domain.chat.dto.response.ChatRoomResponse;
import site.goldenticket.domain.chat.service.ChatService;
import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.*;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.repository.OrderRepository;
import site.goldenticket.domain.payment.service.PaymentService;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NegoServiceImpl implements NegoService {

    private final NegoRepository negoRepository;
    private final ProductService productService;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final AlertService alertService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    @Override
    public NegoResponse confirmPrice(Long negoId, PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + negoId));

        Product product = productService.getProduct(nego.getProductId());

        if (!(product.getProductStatus() == ProductStatus.SELLING)) {
            throw new CustomException("승인한 다른 네고가 있어 승인할수 없습니다.", ErrorCode.NEGO_ALREADY_CONFIRMED);
        }

        if (nego.getStatus() == NegotiationStatus.NEGOTIATING) {
            nego.confirmNego(LocalDateTime.now(), NegotiationStatus.PAYMENT_PENDING,
                    LocalDateTime.now().plusMinutes(20), Boolean.TRUE);
            product.setProductStatus(ProductStatus.RESERVED);
            productService.updateProductForNego(product);
            negoRepository.save(nego);

            //구매자에게 네고 승인 및 결제 안내 알림 전송
            alertService.createAlert(nego.getUser().getId(),
                    "'" + nego.getProduct().getAccommodationName() +
                            "(" + nego.getProduct().getRoomName() + ")'상품에 대한 네고 요청이 승인되었습니다. 해당상품을 "
                            + nego.getExpirationTime() + "까지 결제를 완료해주세요.");
        } else {
            // 다른 상태의 네고는 가격 승낙을 처리할 수 없음
            throw new CustomException("네고를 승인할수 없습니다.", ErrorCode.CANNOT_CONFIRM_NEGO);
        }
        if (nego.getCount() > 3) {
            throw new CustomException("네고를 3회이상 제안하실수 없습니다.", ErrorCode.NEGO_COUNT_OVER);
        }
        return NegoResponse.fromEntity(nego);
    }

    @Override
    public NegoResponse denyPrice(Long negoId, PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        if (nego.getStatus() == NegotiationStatus.NEGOTIATING
                || nego.getStatus() == NegotiationStatus.NEGOTIATION_CANCELLED) {
            nego.setUpdatedAt(LocalDateTime.now());
            nego.setConsent(Boolean.FALSE);

            // 네고 취소 상태로 변경
            if (nego.getCount() == 2) {
                nego.setStatus(NegotiationStatus.NEGOTIATION_CANCELLED);
            }

            negoRepository.save(nego);  // 네고 업데이트

            //구매자에게 네고 거절 알림 전송
            alertService.createAlert(nego.getUser().getId(),
                    "'" + nego.getProduct().getAccommodationName() +
                            "(" + nego.getProduct().getRoomName() + ")'상품에 대한 네고 요청이 거절되었습니다.");

            return NegoResponse.fromEntity(nego);
        } else {
            // NEGOTIATING 상태가 아닌 경우 거절 처리 불가
            throw new CustomException("네고 중인 경우에만 거절할 수 있습니다.",
                    ErrorCode.ONLY_CAN_DENY_WHEN_NEGOTIATING);
        }
    }

    public PriceProposeResponse proposePrice(Long productId, PriceProposeRequest request,
                                             PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Product product = productService.getProduct(productId);
        Long productUserId = product.getUserId();

        if(userId.equals(productUserId)){
            throw new CustomException("자신의 상품에는 네고를 할 수 없습니다.", ErrorCode.CANNOT_NEGOTIATE_SELF_PRODUCT);
        }

        List<Nego> allNegosForProduct = negoRepository.findAllByProduct(product);
        for (Nego nego : allNegosForProduct) {
            if (nego != null && nego.getProduct().getProductStatus() == ProductStatus.SOLD_OUT) {
                throw new CustomException("다른 유저가 네고를 성공해 제안할수 없습니다.", ErrorCode.NEGO_COMPLETED);
            }
            if (nego != null && nego.getStatus() == NegotiationStatus.TRANSFER_PENDING) {
                throw new CustomException("다른 유저가 네고를 성공해 제안할수 없습니다.", ErrorCode.NEGO_COMPLETED);
            }   //OK

            if (nego != null && nego.getStatus() == NegotiationStatus.PAYMENT_PENDING) {
                throw new CustomException("승인된 네고는 가격 제안을 할 수 없습니다.",
                        ErrorCode.NEGO_ALREADY_APPROVED);
            }   //OK
        }

        // 사용자별 상품에 대한 네고 조회
        Nego userNego = negoRepository.findByUserAndProduct(user, product)
                .orElse(new Nego(user, product)); // 네고가 없으면 새로 생성

        if (userNego.getProduct().getProductStatus() == ProductStatus.SOLD_OUT) {
            throw new CustomException("승인된 네고는 가격 제안을 할 수 없습니다.", ErrorCode.NEGO_ALREADY_APPROVED);
        }

        if (userNego.getStatus() == NegotiationStatus.NEGOTIATION_CANCELLED) {
            throw new CustomException("취소된 네고는 가격 제안을 할 수 없습니다.", ErrorCode.CANNOT_NEGOTIATE);
        }

        if (userNego.getStatus() == NegotiationStatus.NEGOTIATION_TIMEOUT) {
            throw new CustomException("20분이 지나 제안할수 없습니다.", ErrorCode.CANNOT_PROPOSE_NEGO);
        }

        // count 증가
        int newCount = userNego.getCount() + 1;

        // 여기서 count가 3인 경우 예외 처리
        if (newCount > 2) {
            throw new CustomException("더 이상 네고할 수 없습니다.", ErrorCode.CANNOT_NEGOTIATE);
        }

        //
        /****
         * 네고를 한적이 있는 사람
         * FALSE인 이유
         네고를 다시 제안한다는것은 네고를 거절했거나 양도를 거절한 케이스
         네고거절, 양도거절을 하면 consent == False가 됨
         => 사용자별 상품에 대한 네고 조회를 한 후 승인여부가 FALSE이면 이전에 네고를 한적이 있는 사람
         */
        if (userNego.getConsent() == Boolean.FALSE) {
            userNego.updateNego(newCount, request.price(), NegotiationStatus.NEGOTIATING,
                    LocalDateTime.now(), LocalDateTime.now(), Boolean.FALSE);

        } else {
            userNego.updateNego(newCount, request.price(), NegotiationStatus.NEGOTIATING,
                    LocalDateTime.now(), LocalDateTime.now(), null);
        }// 처음 네고

        // 네고 저장
        negoRepository.save(userNego);

        //판매자에게 네고 제안 알림 전송
        alertService.createAlert(product.getUserId(),
                "'" + product.getAccommodationName() + "(" + product.getRoomName()
                        + ")'상품에 대한 네고 요청이 들어왔습니다.");

        return PriceProposeResponse.fromEntity(userNego);
    }

    @Override
    public PayResponse pay(Long negoId, PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Nego nego = negoRepository.findById(negoId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        Product product = productService.getProduct(nego.getProductId());

        List<Nego> transferPendingNegos = negoRepository.findAllByProductAndStatus(product, NegotiationStatus.TRANSFER_PENDING);

        if (!transferPendingNegos.isEmpty()) {
            throw new CustomException("양도 대기 중인 상품이 있어 결제할 수 없습니다.", ErrorCode.TRANSFER_PENDING_NEGO);
        }

        if (nego.getStatus() == NegotiationStatus.NEGOTIATION_COMPLETED
                || nego.getStatus() == NegotiationStatus.TRANSFER_PENDING) {
            throw new CustomException("다른 유저가 네고를 성공해 제안할수 없습니다.", ErrorCode.NEGO_COMPLETED);
        }

        if (nego.getConsent()) {
            product.setProductStatus(ProductStatus.RESERVED);
            productService.updateProductForNego(product);
            nego.setStatus(NegotiationStatus.TRANSFER_PENDING);
            nego.setUpdatedAt(LocalDateTime.now());
            negoRepository.save(nego);

            return PayResponse.fromEntity(nego);
        } else {
            throw new CustomException("네고 승인이 필요합니다.", ErrorCode.NEGO_APPROVAL_REQUIRED);
        }
    } //양도 대기중인 상품 있으면 결제 되면 안됨

    @Override
    public HandoverResponse handOverProduct(Long productId, PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        // 해당 Product ID로 Product 정보 가져오기
        Product product = productService.getProduct(productId);

        // Product에 대한 모든 네고 가져오기
        List<Nego> allNegosForProduct = negoRepository.findAllByProduct(product);

        // 양도 대기 중인 네고 찾기
        Optional<Nego> transferPendingNego = allNegosForProduct.stream()
                .filter(nego -> nego.getStatus() == NegotiationStatus.TRANSFER_PENDING)
                .findFirst();

        if (transferPendingNego.isEmpty()) {
            Order order = orderRepository.findByProductIdAndStatus(productId, OrderStatus.WAITING_TRANSFER).orElseThrow(
                    () -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
            );
            checkAccountAndThrowException(user);
            //checkAccountAndThrowException(user);
            product.setProductStatus(ProductStatus.SOLD_OUT);
            productService.updateProductForNego(product);
            handleNegos(allNegosForProduct);
            sendTransferCompleteAlertsForNotNego(order, product, user);
        }

        if (transferPendingNego.isPresent()) {
            checkAccountAndThrowException(user);
            Nego nego = transferPendingNego.get();
            completeTransfer(product, nego);
            handleNegos(allNegosForProduct);
            sendTransferCompleteAlerts(nego, product, user);
            return HandoverResponse.fromEntity(product, nego);
        }
        return null;
    }

    @Override
    public NegoResponse denyHandoverProduct(Long productId, PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();

        // 해당 Product ID로 Product 정보 가져오기
        Product product = productService.getProduct(productId);

        //구매자 환불
        Order order = orderRepository.findByProductIdAndStatus(productId, OrderStatus.WAITING_TRANSFER).orElseThrow(
                () -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
        );
        paymentService.cancelPayment(paymentService.findByOrderId(order.getId()).getImpUid());


        // Product에 대한 모든 네고 가져오기
        List<Nego> allNegosForProduct = negoRepository.findAllByProduct(product);

        // 양도 대기 중인 네고 찾기
        Optional<Nego> transferPendingNego = allNegosForProduct.stream()
                .filter(nego -> nego.getStatus() == NegotiationStatus.TRANSFER_PENDING)
                .findFirst();

        if (transferPendingNego.isEmpty()) {
            updateProductForDenyHandOver(product);

            //구매자에게 양도 취소 알림 전송
            alertService.createAlert(order.getUserId(),
                    "판매자 사정으로 양도가 취소되었습니다. 결제 금액이 100% 환불됩니다.");
            //판매자에게 양도 취소 알림 전송
            alertService.createAlert(product.getUserId(),
                    "양도가 취소되었습니다. 구매자에게 결제 금액이 100% 환불됩니다.");
            //해당 상품 찜한 회원들에게 알림 전송
            if(product.getProductStatus().equals(ProductStatus.SELLING)) {
                alertService.createAlertOfWishProductToSelling(productId, product.getAccommodationName(), product.getRoomName());
            }
        }

        if (transferPendingNego.isPresent()) {
            Nego nego = transferPendingNego.get();

            // 양도 대기 중인 네고를 찾았을 경우 거절 처리
            if (nego.getCount() < 2) {
                nego.setStatus(NegotiationStatus.NEGOTIATING);
            } else {
                nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
            }
            nego.setConsent(Boolean.FALSE);
            nego.setExpirationTime(LocalDateTime.now());
            updateProductForDenyHandOver(product);
            negoRepository.save(nego);

            //구매자에게 양도 취소 알림 전송
            alertService.createAlert(nego.getUser().getId(),
                    "판매자 사정으로 양도가 취소되었습니다. 결제 금액이 100% 환불됩니다.");
            //판매자에게 양도 취소 알림 전송
            alertService.createAlert(product.getUserId(),
                    "양도가 취소되었습니다. 구매자에게 결제 금액이 100% 환불됩니다.");
            //해당 상품 찜한 회원들에게 알림 전송
            if(product.getProductStatus().equals(ProductStatus.SELLING)) {
                alertService.createAlertOfWishProductToSelling(productId, product.getAccommodationName(), product.getRoomName());
            }

            return NegoResponse.fromEntity(nego);
        }
        return null;
    }

    // 메서드
    private void updateProductForDenyHandOver(Product product) {
        product.setProductStatus(ProductStatus.SELLING);
        productService.updateProductForNego(product);
    }

    private void handleNegos(List<Nego> allNegosForProduct) {
        for (Nego otherNego : allNegosForProduct) {
            otherNego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
            otherNego.setConsent(Boolean.FALSE);
            negoRepository.save(otherNego);
        }
    }

    private void completeTransfer(Product product, Nego nego) {
        nego.setUpdatedAt(LocalDateTime.now());
        nego.setConsent(Boolean.TRUE);
        nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
        product.setProductStatus(ProductStatus.SOLD_OUT);
        product.setGoldenPrice(nego.getPrice());
        productService.updateProductForNego(product);
        negoRepository.save(nego);
    }

    private void checkAccountAndThrowException(User user) {
        if (user.getAccountNumber() == null) {
            throw new CustomException("등록된 계좌가 없습니다.", ErrorCode.NO_REGISTERED_ACCOUNT);
        }
    }

    private void sendTransferCompleteAlerts(Nego nego, Product product, User user) {
        // 구매자에게 양도 완료 알림 전송
        alertService.createAlert(nego.getUser().getId(),
                "'" + product.getAccommodationName() + "(" + product.getRoomName()
                        + ")'상품 양도가 완료되었습니다. "
                        + "양도 완료에 따른 체크인 정보는 '마이페이지 > 구매내역 > 구매 완료'에서 확인하실 수 있습니다.");

        // 판매자에게 정산 요청 알림 전송
        alertService.createAlert(product.getUserId(),
                "'" + product.getAccommodationName() + "(" + product.getRoomName()
                        + ")'상품 양도가 완료되었습니다. 영업일 1일 이내 등록한 계좌 정보로 정산 금액이 입금됩니다."
                        + "원활한 정산 진행을 위해 '마이페이지 - 나의 계좌'정보를 다시 한번 확인해주세요.");

        // 판매자에게 계좌 등록 알림 전송
        if (user != null && user.getAccountNumber() == null) {
            alertService.createAlert(product.getUserId(),
                    "'" + product.getAccommodationName() + "(" + product.getRoomName()
                            + ")'상품에 대한 원활한 정산을 위해 '마이페이지 > 내 계좌'에서 입금받으실 계좌를 등록해주세요.");
        }
    }

    private void sendTransferCompleteAlertsForNotNego(Order order, Product product, User user) {
        // order 객체가 null이 아닌 경우에만 실행
        if (order != null && user != null) {
            // 구매자에게 양도 완료 알림 전송
            alertService.createAlert(order.getUserId(),
                    "'" + product.getAccommodationName() + "(" + product.getRoomName()
                            + ")'상품 양도가 완료되었습니다. "
                            + "양도 완료에 따른 체크인 정보는 '마이페이지 > 구매내역 > 구매 완료'에서 확인하실 수 있습니다.");

            // 판매자에게 정산 요청 알림 전송
            alertService.createAlert(product.getUserId(),
                    "'" + product.getAccommodationName() + "(" + product.getRoomName()
                            + ")'상품 양도가 완료되었습니다. 영업일 1일 이내 등록한 계좌 정보로 정산 금액이 입금됩니다."
                            + "원활한 정산 진행을 위해 '마이페이지 - 나의 계좌'정보를 다시 한번 확인해주세요.");

            // 판매자에게 계좌 등록 알림 전송
            if (user.getAccountNumber() == null) {
                alertService.createAlert(product.getUserId(),
                        "'" + product.getAccommodationName() + "(" + product.getRoomName()
                                + ")'상품에 대한 원활한 정산을 위해 '마이페이지 > 내 계좌'에서 입금받으실 계좌를 등록해주세요.");
            }
        }
    }

    @Override
    public List<Nego> getUserNego(Long userId) {
        return negoRepository.findNegoByUser_Id(userId);
    }

    @Override
    public Nego save(Nego nego) {
        return negoRepository.save(nego);
    }

    /***
     * 네고 가능 여부 조회
     * @param userId 회원 ID
     * @param productId 상품 ID
     * @return 네고 가능 여부 응답 DTO (네고 가능 여부, 채팅방 ID)
     */
    public NegoAvailableResponse isAvailableNego(Long userId, Long productId) {
        Boolean negoAvailable = true;
        Long chatRoomId = -1L;
        Product product = productService.getProduct(productId);
        //본인이 판매하는 상품이면 네고 불가
        if (product.getUserId().equals(userId)) {
            negoAvailable = false;
        }
        //판매중인 상품인지 확인: 판매중이 아니면 네고 불가
        if (!product.getProductStatus().equals(ProductStatus.SELLING)) {
            negoAvailable = false;

        } else {
            if (!negoRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
                //네고 이력 없는 경우 : 채팅방 생성 + 채팅방 시작 메세지 생성 + 네고 가능
                if (!chatService.existsChatRoomByBuyerIdAndProductId(userId, productId)) {
                    ChatRoomResponse chatRoomResponse = chatService.createChatRoom(userId, productId);
                    chatService.createStartMessageOfNewChatRoom(chatRoomResponse.chatRoomId());
                    negoAvailable = true;
                }
            } else {
                //네고 이력 있는 경우 : 2차 네고(거절 혹은 승인) OR 재결제 -> 네고 불가
                List<Nego> negoList = negoRepository.findAllByUser_IdAndProduct_Id(userId,
                        productId);
                for (Nego nego : negoList) {
                    if (nego.getCount().equals(2) || nego.getStatus()
                            .equals(NegotiationStatus.NEGOTIATION_TIMEOUT)) {
                        negoAvailable = false;
                        break;
                    }
                }
            }
        }
        if (negoAvailable) {
            chatRoomId = chatService.getChatRoomByBuyerIdAndProductId(userId, productId).getId();
        }
        return NegoAvailableResponse.builder()
                .negoAvailable(negoAvailable)
                .chatRoomId(chatRoomId)
                .build();
    }

    /***
     * 테스트용 네고 기록 조회  (프론트 DB 확인용)
     * @return 네고 Entity List 응답 DTO
     */
    public NegoTestListResponse getNegoListForTest() {
        List<Nego> negoList = negoRepository.findAll();
        List<NegoTestResponse> negoTestResponseList = new ArrayList<>();
        for (Nego nego : negoList) {
            negoTestResponseList.add(NegoTestResponse.fromEntity(nego));
        }
        return NegoTestListResponse.builder()
                .negoTestResponseList(negoTestResponseList).build();
    }

    public List<Nego> findByStatusInAndProduct(List<NegotiationStatus> negotiationStatusList,
                                               Product product) {
        return negoRepository.findByStatusInAndProduct(negotiationStatusList, product);
    }
}
