package site.goldenticket.domain.product.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import site.goldenticket.domain.product.dto.*;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static site.goldenticket.common.constants.OrderStatus.COMPLETED_TRANSFER;
import static site.goldenticket.common.redis.constants.RedisConstants.AUTOCOMPLETE_KEY;

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
    public ProductDetailResponse getProduct(
            Long productId, PrincipalDetails principalDetails, HttpServletRequest request, HttpServletResponse response
    ) {
        Long userId = (principalDetails != null) ? principalDetails.getUserId() : null;

        Product product = (userId != null) ? productService.getProductWithWishProducts(productId, userId) : productService.getProduct(productId);

        boolean isAuthenticated = (userId != null);

        String userKey = isAuthenticated ? principalDetails.getUsername() : productService.generateOrRetrieveAnonymousKey(request, response);

        boolean isSeller = isAuthenticated && principalDetails.getUserId().equals(product.getUserId());
        boolean isTrading = getOrdersForProduct(productId).isPresent() || !getNegotiationsForProduct(product).isEmpty();

        productService.updateProductViewCount(userKey, productId.toString());
        productService.updateAutocompleteCount(AUTOCOMPLETE_KEY, product.getAccommodationName());

        return ProductDetailResponse.fromEntity(product, isSeller, isTrading, isAuthenticated);
    }

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
            Optional<Order> optionalOrder = getOrdersForProduct(productId);

            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();

                ProgressProductStatus progressProductStatus = ProgressProductStatus.valueOf(String.valueOf(order.getStatus()));
                progressProductStatusSet.add(progressProductStatus);

                Long orderUserId = order.getUserId();
                User orderUser = userService.findById(orderUserId);

                ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(orderUserId, productId);
                progressChatResponseList.add(
                        new ProgressChatResponse(
                                chatRoom.getId(),
                                orderUser.getNickname(),
                                orderUser.getImageUrl(),
                                order.getPrice(),
                                ChatRoomStatus.YELLOW_DOT,
                                chatService.getChatList(chatRoom.getId(), orderUser.getId()).get(0).getUpdatedAt()
                        )
                );
            }

            // 2.2 네고
            List<Nego> negoList = getNegotiationsForProduct(product);

            for (Nego nego : negoList) {
                NegotiationStatus negotiationStatus = nego.getStatus();

                if(negotiationStatus != NegotiationStatus.NEGOTIATION_CANCELLED && negotiationStatus != NegotiationStatus.NEGOTIATION_TIMEOUT) {
                    ProgressProductStatus progressProductStatus = ProgressProductStatus.valueOf(String.valueOf(nego.getStatus()));
                    progressProductStatusSet.add(progressProductStatus);
                }

                if(negotiationStatus == NegotiationStatus.NEGOTIATION_TIMEOUT) {
                    progressProductStatusSet.add(ProgressProductStatus.NEGOTIATING);
                }

                User user = nego.getUser();

                ChatRoomStatus chatRoomStatus;
                if (negotiationStatus == NegotiationStatus.NEGOTIATING) {
                    chatRoomStatus = ChatRoomStatus.ACTIVE;
                } else if (negotiationStatus == NegotiationStatus.PAYMENT_PENDING) {
                    chatRoomStatus = ChatRoomStatus.YELLOW_DOT;
                } else if (negotiationStatus == NegotiationStatus.NEGOTIATION_CANCELLED) {
                    chatRoomStatus = ChatRoomStatus.INACTIVE;
                } else {
                    throw new CustomException(ErrorCode.COMMON_SYSTEM_ERROR);
                }

                ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(user.getId(), productId);
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

    @Transactional(readOnly = true)
    public List<ProductCompletedHistoryResponse> getAllCompletedProducts(Long userId) {
        List<ProductStatus> productStatusList = Arrays.asList(ProductStatus.SOLD_OUT, ProductStatus.EXPIRED);
        List<Product> productList = productService.findByProductStatusInAndUserId(productStatusList, userId);

        return productList.stream()
                .filter(product -> !product.isSellerViewCheck())
                .map(product -> ProductCompletedHistoryResponse.fromEntity(product, product.getProductStatus()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductCompletedSoldOutResponse getSoldOutCaseProductDetails(Long productId) {
        Product product = productService.findByProductStatusAndProductId(ProductStatus.SOLD_OUT, productId);

        Order order = paymentService.findByProductIdAndStatus(productId, COMPLETED_TRANSFER)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_COMPLETED_TRANSFER_ORDER));

        Long userId = order.getUserId();
        User user = userService.findById(userId);

        ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(userId, productId);
        LocalDateTime lastUpdatedAt = chatService.getChatList(chatRoom.getId(), user.getId()).get(0).getUpdatedAt();

        return ProductCompletedSoldOutResponse.fromEntity(product, order, user, chatRoom, lastUpdatedAt);
    }

    @Transactional(readOnly = true)
    public ProductCompletedExpiredResponse getExpiredCaseProductDetails(Long productId) {
        Product product = productService.findByProductStatusAndProductId(ProductStatus.EXPIRED, productId);

        return ProductCompletedExpiredResponse.fromEntity(product);
    }

    @Transactional
    public Long deleteCompletedProduct(Long productId) {
        Product product = productService.getProduct(productId);
        product.setIsSellerViewCheck(true);
        return productId;
    }

    public Optional<Order> getOrdersForProduct(Long productId) {
        return paymentService.findByProductIdAndStatus(productId, OrderStatus.WAITING_TRANSFER);
    }

    private List<Nego> getNegotiationsForProduct(Product product) {
        List<NegotiationStatus> negotiationStatusList = Arrays.asList(NegotiationStatus.NEGOTIATING, NegotiationStatus.PAYMENT_PENDING, NegotiationStatus.NEGOTIATION_CANCELLED, NegotiationStatus.NEGOTIATION_TIMEOUT);
        return negoService.findByStatusInAndProduct(negotiationStatusList, product);
    }
}
