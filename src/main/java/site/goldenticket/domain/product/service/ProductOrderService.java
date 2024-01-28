package site.goldenticket.domain.product.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.chat.constants.ChatRoomStatus;
import site.goldenticket.domain.chat.dto.response.ProgressChatResponse;
import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.chat.service.ChatService;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.service.NegoService;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.service.PaymentService;
import site.goldenticket.domain.product.constants.NegoProductStatus;
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

import static site.goldenticket.common.constants.OrderStatus.*;
import static site.goldenticket.common.redis.constants.RedisConstants.*;
import static site.goldenticket.common.response.ErrorCode.*;
import static site.goldenticket.domain.chat.constants.ChatRoomStatus.*;
import static site.goldenticket.domain.nego.status.NegotiationStatus.NEGOTIATING;
import static site.goldenticket.domain.nego.status.NegotiationStatus.*;
import static site.goldenticket.domain.nego.status.NegotiationStatus.NEGOTIATION_CANCELLED;
import static site.goldenticket.domain.product.constants.NegoProductStatus.*;
import static site.goldenticket.domain.product.constants.ProductStatus.*;

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

        NegoProductStatus negoProductStatus = null;
        if (isSeller) {
            List<Nego> negoList = negoService.findAllByProductAndStatus(product, NEGOTIATING);

            if (!negoList.isEmpty()) {
                negoProductStatus = NEGOTIATION_HAVE;
            }
        }
        else {
            if(isAuthenticated) {
                Optional<Nego> optionalNego = negoService.findByUserIdAndProduct(userId, product);
                if(optionalNego.isPresent()) {
                    Nego nego = optionalNego.get();
                    if (List.of(NEGOTIATING, NEGOTIATION_CANCELLED).contains(nego.getStatus())) {
                        negoProductStatus = NegoProductStatus.valueOf(String.valueOf(nego.getStatus()));
                    }
                }
            }
        }

        productService.updateProductViewCount(userKey, productId.toString());
        productService.updateAutocompleteCount(AUTOCOMPLETE_KEY, product.getAccommodationName());

        return ProductDetailResponse.fromEntity(product, isSeller, negoProductStatus, isAuthenticated);
    }

    @Transactional(readOnly = true)
    public List<ProductProgressHistoryResponse> getProgressProducts(Long userId) {
        List<ProductProgressHistoryResponse> productProgressHistoryResponseList = new ArrayList<>();

        // 1. 판매중에 해당하는 상품 정보
        List<ProductStatus> productStatusList = Arrays.asList(SELLING, RESERVED);
        List<Product> productList = productService.findByProductStatusInAndUserId(productStatusList, userId);

        // 2. 모든 상품들 조회
        for (Product product : productList) {
            Long productId = product.getId();

            Set<ProgressProductStatus> progressProductStatusSet = new HashSet<>();
            List<ProgressChatResponse> progressChatResponseList = new ArrayList<>();

            // 2.1 주문
            Optional<Order> optionalOrder = paymentService.findByProductIdAndStatus(productId, WAITING_TRANSFER);

            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();

                ProgressProductStatus progressProductStatus = ProgressProductStatus.valueOf(String.valueOf(order.getStatus()));
                progressProductStatusSet.add(progressProductStatus);

                Long buyerId = order.getUserId();
                User buyer = userService.findById(buyerId);

                ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(buyerId, productId);
                progressChatResponseList.add(
                        new ProgressChatResponse(
                                chatRoom.getId(),
                                buyer.getNickname(),
                                buyer.getImageUrl(),
                                order.getPrice(),
                                YELLOW_DOT,
                                chatService.getFirstChatUpdatedAt(chatRoom.getId(), buyer.getId())
                        )
                );
            }

            // 2.2 네고
            List<NegotiationStatus> negotiationStatusList = Arrays.asList(NEGOTIATING, PAYMENT_PENDING, NEGOTIATION_CANCELLED, NEGOTIATION_TIMEOUT);
            List<Nego> negoList =  negoService.findByStatusInAndProduct(negotiationStatusList, product);

            for (Nego nego : negoList) {
                NegotiationStatus negotiationStatus = nego.getStatus();

                if(negotiationStatus != NEGOTIATION_CANCELLED && negotiationStatus != NEGOTIATION_TIMEOUT) {
                    ProgressProductStatus progressProductStatus = ProgressProductStatus.valueOf(String.valueOf(nego.getStatus()));
                    progressProductStatusSet.add(progressProductStatus);
                }

                if(negotiationStatus == NEGOTIATION_TIMEOUT) {
                    progressProductStatusSet.add(ProgressProductStatus.NEGOTIATING);
                }

                User buyer = nego.getUser();
                Long buyerId = buyer.getId();

                ChatRoomStatus chatRoomStatus;
                if (negotiationStatus == NEGOTIATING || negotiationStatus == NEGOTIATION_TIMEOUT) {
                    chatRoomStatus = ACTIVE;
                } else if (negotiationStatus == PAYMENT_PENDING) {
                    chatRoomStatus = YELLOW_DOT;
                } else if (negotiationStatus == NEGOTIATION_CANCELLED) {
                    chatRoomStatus = INACTIVE;
                } else {
                    throw new CustomException(COMMON_SYSTEM_ERROR);
                }

                ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(buyerId, productId);
                progressChatResponseList.add(
                        new ProgressChatResponse(
                                chatRoom.getId(),
                                buyer.getNickname(),
                                buyer.getImageUrl(),
                                nego.getPrice(),
                                chatRoomStatus,
                                chatService.getFirstChatUpdatedAt(chatRoom.getId(), buyerId)
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
        List<ProductStatus> productStatusList = Arrays.asList(SOLD_OUT, EXPIRED);
        List<Product> productList = productService.findByProductStatusInAndUserId(productStatusList, userId);

        return productList.stream()
                .filter(product -> !product.isSellerViewCheck())
                .map(product -> ProductCompletedHistoryResponse.fromEntity(product, product.getProductStatus()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductCompletedSoldOutResponse getSoldOutCaseProductDetails(Long productId) {
        Product product = productService.findByProductStatusAndProductId(SOLD_OUT, productId);

        Order order = paymentService.findByProductIdAndStatus(productId, COMPLETED_TRANSFER)
                .orElseThrow(() -> new CustomException(NO_COMPLETED_TRANSFER_ORDER));

        Long buyerId = order.getUserId();
        User buyer = userService.findById(buyerId);

        ChatRoom chatRoom = chatService.getChatRoomByBuyerIdAndProductId(buyerId, productId);
        LocalDateTime lastUpdatedAt = chatService.getFirstChatUpdatedAt(chatRoom.getId(), buyerId);

        return ProductCompletedSoldOutResponse.fromEntity(product, order, buyer, chatRoom, lastUpdatedAt);
    }

    @Transactional(readOnly = true)
    public ProductCompletedExpiredResponse getExpiredCaseProductDetails(Long productId) {
        Product product = productService.findByProductStatusAndProductId(EXPIRED, productId);

        return ProductCompletedExpiredResponse.fromEntity(product);
    }

    @Transactional
    public Long deleteCompletedProduct(Long productId) {
        Product product = productService.getProduct(productId);
        product.setIsSellerViewCheck(true);
        return productId;
    }
}
