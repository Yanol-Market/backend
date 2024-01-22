package site.goldenticket.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.chat.service.ChatService;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.service.NegoService;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.payment.dto.response.PurchaseCompletedDetailResponse;
import site.goldenticket.domain.payment.dto.response.PurchaseCompletedResponse;
import site.goldenticket.domain.payment.dto.response.PurchaseProgressResponse;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseHistoryService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final NegoService negoService;
    private final ChatService chatService;
    private final ProductService productService;

    public List<PurchaseProgressResponse> getPurchaseProgressHistory(PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();

        List<Nego> userNego = negoService.getUserNego(userId);

        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, OrderStatus.WAITING_TRANSFER);//양도대기중

        List<PurchaseProgressResponse> purchaseProgressResponses = new ArrayList<>();

        if (orders.isEmpty() && userNego.isEmpty()) {
            return Collections.emptyList();
        }

        if (!orders.isEmpty()) {
            for (Order order : orders) {
                Product product = productService.getProduct(order.getProductId());
                User user = userService.findById(product.getUserId());
                ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(userId, product.getId());
                LocalDateTime lastUpdatedAt = chatService.getChatList(chatRoom.getId(), userId).get(0).getCreatedAt();
                PurchaseProgressResponse response = PurchaseProgressResponse.create(product, "TRANSFER_PENDING", user, chatRoom.getId(), order.getPrice(), lastUpdatedAt);
                purchaseProgressResponses.add(response);
            }
        }

        if (!userNego.isEmpty()) {
            for (Nego nego : userNego) {
                if (nego.getStatus() == NegotiationStatus.NEGOTIATING) {
                    Product product = productService.getProduct(nego.getProductId());
                    User user = userService.findById(product.getUserId());
                    ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(userId, product.getId());
                    LocalDateTime lastUpdatedAt = chatService.getChatList(chatRoom.getId(), userId).get(0).getCreatedAt();
                    PurchaseProgressResponse response = PurchaseProgressResponse.create(product, "NEGOTIATING", user, chatRoom.getId(), nego.getPrice(), lastUpdatedAt);
                    purchaseProgressResponses.add(response);
                }
                if (nego.getStatus() == NegotiationStatus.PAYMENT_PENDING) {
                    Product product = productService.getProduct(nego.getProductId());
                    User user = userService.findById(product.getUserId());
                    ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(userId, product.getId());
                    LocalDateTime lastUpdatedAt = chatService.getChatList(chatRoom.getId(), userId).get(0).getCreatedAt();
                    PurchaseProgressResponse response = PurchaseProgressResponse.create(product, "PAYMENT_PENDING", user, chatRoom.getId(), nego.getPrice(), lastUpdatedAt);
                    purchaseProgressResponses.add(response);
                }
            }
        }
        return purchaseProgressResponses;
    }

    public List<PurchaseCompletedResponse> getPurchaseCompletedHistory(PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, OrderStatus.COMPLETED_TRANSFER);//양도완료

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<PurchaseCompletedResponse> purchaseCompletedResponses = new ArrayList<>();

        for (Order order : orders) {
            if(order.isCustomerViewCheck()){
                continue;
            }
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
        User seller = userService.findById(chatRoom.getBuyerId());
        LocalDateTime lastUpdatedAt = chatService.getChatList(userId, chatRoom.getId()).get(0).getCreatedAt();
        return PurchaseCompletedDetailResponse.create(product, order, payment, seller, chatRoom.getId(), lastUpdatedAt);
    }

    @Transactional
    public Long deletePurchaseCompletedHistory(Long orderId, PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
        );

        if (!order.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.USER_ORDER_NOT_MATCH);
        }

        if(!order.isCustomerViewCheck()){
            order.changeViewCheck();
        }

        return orderId;
    }
}
