package site.goldenticket.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.chat.constants.ChatRoomStatus;
import site.goldenticket.domain.chat.dto.response.ProgressChatResponse;
import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.chat.service.ChatService;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.service.NegoService;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.service.PaymentService;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.constants.ProgressProductStatus;
import site.goldenticket.domain.product.dto.ProductProgressHistoryResponse;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.service.UserService;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOrderService {

    private final ProductService productService;
    private final UserService userService;
    private final ChatService chatService;
    private final NegoService negoService;
    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public List<ProductProgressHistoryResponse> getProgressProducts(Long userId) {
        List<ProductProgressHistoryResponse> productProgressHistoryResponseList = new ArrayList<>();

        // 1. 판매중에 해당하는 상품 정보
        List<ProductStatus> productStatusList = Arrays.asList(ProductStatus.SELLING, ProductStatus.RESERVED);
        List<Product> productList = productService.findByProductStatusInAndUserId(productStatusList, userId);

        // 2. 모든 상품들 조회
        for (Product product : productList) {
            Long productId = product.getId();

            Set<ProgressProductStatus> progressProductStatusSet = new HashSet<>();
            List<ProgressChatResponse> progressChatResponseList = new ArrayList<>();

            // 2.1 주문
            List<Order> orderList = paymentService.findAllByStatus(OrderStatus.COMPLETED_TRANSFER);

            for (Order order : orderList) {
                ProgressProductStatus progressProductStatus = ProgressProductStatus.valueOf(String.valueOf(order.getStatus()));
                progressProductStatusSet.add(progressProductStatus);

                Long orderUserId = order.getUserId();
                User orderUser = userService.findById(orderUserId);

                ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(productId, orderUserId);
                progressChatResponseList.add(
                        new ProgressChatResponse(
                                chatRoom.getId(),
                                orderUser.getNickname(),
                                orderUser.getImageUrl(),
                                order.getPrice(),
                                ChatRoomStatus.ACTIVE,
                                chatService.getChatList(chatRoom.getId(), orderUser.getId()).get(0).getUpdatedAt()
                        )
                );
            }

            // 2.2 네고
            List<NegotiationStatus> negotiationStatusList = Arrays.asList(NegotiationStatus.NEGOTIATING, NegotiationStatus.PAYMENT_PENDING, NegotiationStatus.NEGOTIATION_CANCELLED);
            List<Nego> negoList = negoService.findAllByStatusIn(negotiationStatusList);

            for (Nego nego : negoList) {
                NegotiationStatus negotiationStatus = nego.getStatus();

                if(negotiationStatus != NegotiationStatus.NEGOTIATION_CANCELLED) {
                    ProgressProductStatus progressProductStatus = ProgressProductStatus.valueOf(String.valueOf(nego.getStatus()));
                    progressProductStatusSet.add(progressProductStatus);
                }

                User user = nego.getUser();

                ChatRoomStatus chatRoomStatus;
                if (negotiationStatus == NegotiationStatus.NEGOTIATING || negotiationStatus == NegotiationStatus.PAYMENT_PENDING) {
                    chatRoomStatus = ChatRoomStatus.YELLOW_DOT;
                } else if (negotiationStatus == NegotiationStatus.NEGOTIATION_CANCELLED) {
                    chatRoomStatus = ChatRoomStatus.INACTIVE;
                } else {
                    throw new CustomException(ErrorCode.COMMON_SYSTEM_ERROR);
                }

                ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(productId, user.getId());
                progressChatResponseList.add(
                        new ProgressChatResponse(
                                chatRoom.getId(),
                                user.getNickname(),
                                user.getImageUrl(),
                                nego.getPrice(),
                                chatRoomStatus,
                                chatService.getChatList(chatRoom.getId(), user.getId()).get(0).getUpdatedAt()
                        )
                );
            }

            List<ProgressProductStatus> progressProductStatusList = new ArrayList<>(progressProductStatusSet);
            ProgressProductStatus progressProductStatus = progressProductStatusList.stream()
                    .sorted(Comparator.reverseOrder())
                    .findFirst()
                    .orElse(null);

            ProductProgressHistoryResponse productProgressHistoryResponse = ProductProgressHistoryResponse.fromEntity(product, progressProductStatus, progressChatResponseList);
            productProgressHistoryResponseList.add(productProgressHistoryResponse);

        }
        return productProgressHistoryResponseList;
    }
}
