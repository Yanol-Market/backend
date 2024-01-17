package site.goldenticket.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.chat.service.ChatService;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.service.NegoService;
import site.goldenticket.domain.payment.dto.response.PurchaseCompletedDetailResponse;
import site.goldenticket.domain.payment.dto.response.PurchaseCompletedResponse;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.model.Payment;
import site.goldenticket.domain.payment.repository.OrderRepository;
import site.goldenticket.domain.payment.repository.PaymentRepository;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseHistoryService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final NegoService negoService;
    private final ChatService chatService;
    private final ProductService productService;

    public String getPurchaseProgressHistory(PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();
        //결제 햇는지 확인
        List<Order> orders = orderRepository.findByUserId(userId);
        if (!orders.isEmpty()) {
            for (Order order : orders) {
                ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(userId, order.getProductId());
            }
        }

        //네고 중인지 확인
        Optional<Nego> nego = negoService.getUserNego(userId);

    }

    public List<PurchaseCompletedResponse> getPurchaseCompletedHistory(PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, OrderStatus.COMPLETED_TRANSFER);

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<PurchaseCompletedResponse> purchaseCompletedResponses = new ArrayList<>();

        for (Order order : orders) {
            Product product = productService.getProduct(order.getProductId());
            PurchaseCompletedResponse response = PurchaseCompletedResponse.create(product, order);
            purchaseCompletedResponses.add(response);
        }

        return purchaseCompletedResponses;
    }

    public PurchaseCompletedDetailResponse getPurchaseCompletedHistoryDetail(Long orderId, PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
        );

        if (!order.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.USER_ORDER_NOT_MATCH);
        }

        Product product = productService.getProduct(order.getProductId());
        Payment payment = paymentRepository.findByOrderId(order.getId());
        ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(userId, order.getProductId());
        User seller = userService.getUser(chatRoom.getUserId());
        LocalDateTime lastUpdatedAt = chatService.getChatList(userId, chatRoom.getId()).getFirst().getCreatedAt();
        return PurchaseCompletedDetailResponse.create(product, order, payment, seller, chatRoom.getId(), lastUpdatedAt);
    }
}
