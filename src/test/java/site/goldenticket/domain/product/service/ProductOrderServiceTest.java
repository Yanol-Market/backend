package site.goldenticket.domain.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.chat.service.ChatService;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.service.NegoService;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.service.PaymentService;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.dto.*;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static site.goldenticket.common.constants.OrderStatus.COMPLETED_TRANSFER;
import static site.goldenticket.common.constants.OrderStatus.WAITING_TRANSFER;
import static site.goldenticket.common.utils.ChatRoomUtils.createChatRoom;
import static site.goldenticket.common.utils.NegoUtils.createNego;
import static site.goldenticket.common.utils.OrderUtils.createOrder;
import static site.goldenticket.common.utils.ProductUtils.createProduct;
import static site.goldenticket.common.utils.UserUtils.PASSWORD;
import static site.goldenticket.common.utils.UserUtils.createUser;
import static site.goldenticket.domain.nego.status.NegotiationStatus.*;
import static site.goldenticket.domain.product.constants.ProductStatus.*;

@ExtendWith(MockitoExtension.class)
public class ProductOrderServiceTest {

    @InjectMocks
    private ProductOrderService productOrderService;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ChatService chatService;

    @Mock
    private NegoService negoService;

    @Mock
    private UserService userService;

    public static final String ANONOYMOUS_KEY = "anonymousKey";

    // user
    private Long sellerId;
    private Long buyerId;
    private User seller;
    private User buyer;

    // product
    private Long sellingProductId;
    private Long reservedProductId;
    private Long soldOutProductId;
    private Long expiredProductId;

    private Product sellingProduct;
    private Product reservedProduct;
    private Product soldOutProduct;
    private Product expiredProduct;

    // chatRoom
    Long chatRoomId;

    @BeforeEach
    void setUp() {
        // user
        sellerId = 1L;
        buyerId = 2L;

        seller = createUser(PASSWORD);
        seller.setId(sellerId);

        buyer = createUser(PASSWORD);
        buyer.setId(buyerId);


        // product
        sellingProductId = 1L;
        reservedProductId = 2L;
        soldOutProductId = 2L;
        expiredProductId = 4L;

        sellingProduct = createProduct();
        sellingProduct.setId(sellingProductId);
        sellingProduct.setProductStatus(SELLING);

        reservedProduct = createProduct();
        reservedProduct.setId(reservedProductId);
        reservedProduct.setProductStatus(RESERVED);

        soldOutProduct = createProduct();
        soldOutProduct.setId(soldOutProductId);
        soldOutProduct.setProductStatus(SOLD_OUT);

        expiredProduct = createProduct();
        expiredProduct.setId(expiredProductId);
        expiredProduct.setProductStatus(EXPIRED);

        // chatRoom
        chatRoomId = 1L;
    }

    @Test
    void getProduct() {
        // given
        setUpProduct(sellerId, sellingProduct);

        // when
        ProductDetailResponse result = productOrderService.getProduct(sellingProductId, new PrincipalDetails(seller), new MockHttpServletRequest(), new MockHttpServletResponse());
    }

    @Test
    void getProgressProducts() {
        // given
        setUpBuyer();

        List<ProductStatus> productStatusList = Arrays.asList(SELLING, RESERVED);
        List<Product> productList = Arrays.asList(sellingProduct, reservedProduct);
        setUpProducts(productStatusList, productList);

        List<OrderStatus> orderStatusList = Collections.singletonList(WAITING_TRANSFER);
        setUpOrders(orderStatusList, productList);

        List<NegotiationStatus> negotiationStatusList = Arrays.asList(
                NEGOTIATING, PAYMENT_PENDING, NEGOTIATION_CANCELLED, NEGOTIATION_TIMEOUT
        );
        setUpNegotiations(negotiationStatusList, productList);

        setUpChatRooms(productList);

        // when
        List<ProductProgressHistoryResponse> result = productOrderService.getProgressProducts(sellerId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getAllCompletedProducts() {
        // given
        List<ProductStatus> productStatusList = Arrays.asList(SOLD_OUT, EXPIRED);
        List<Product> productList = Arrays.asList(soldOutProduct, expiredProduct);
        setUpProducts(productStatusList, productList);

        // when
        List<ProductCompletedHistoryResponse> result = productOrderService.getAllCompletedProducts(sellerId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
    }

    @Test
    void getSoldOutCaseProductDetails() {
        // given
        setUpBuyer();
        setUpProduct(SOLD_OUT, soldOutProduct);
        setUpOrder(COMPLETED_TRANSFER, soldOutProduct);
        setUpChatRoom(soldOutProduct);

        // when
        ProductCompletedSoldOutResponse result = productOrderService.getSoldOutCaseProductDetails(soldOutProductId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getExpiredCaseProductDetails() {
        // given
        setUpProduct(EXPIRED, expiredProduct);

        // when
        ProductCompletedExpiredResponse result = productOrderService.getExpiredCaseProductDetails(expiredProductId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void deleteCompletedProduct() {
        // given
        setUpProduct(sellerId, soldOutProduct);

        // when
        Long result = productOrderService.deleteCompletedProduct(soldOutProductId);

        // then
        assertThat(result).isNotNull();
    }

    private void setUpBuyer() {
        when(userService.findById(
                buyerId
        ))
                .thenReturn(buyer);
    }

    private void setUpProduct(
            Long userId,
            Product product
    ) {
        Long productId = product.getId();
        when(productService.getProductWithWishProducts(
                productId,
                userId
        ))
                .thenReturn(product);
    }

    private void setUpProduct(
            ProductStatus productStatus,
            Product product
    ) {
        Long productId = product.getId();

        when(productService.findByProductStatusAndProductId(
                productStatus,
                productId
        ))
                .thenReturn(product);
    }

    private void setUpProducts(
            List<ProductStatus> productStatusList,
            List<Product> productList
    ) {
        when(productService.findByProductStatusInAndUserId(
                productStatusList,
                sellerId
        ))
                .thenReturn(productList);
    }

    private void setUpOrder(
            OrderStatus orderStatus,
            Product product
    ) {
        Long productId = product.getId();

        Order order = createOrder(product, buyer);
        order.setStatus(orderStatus);

        when(paymentService.findByProductIdAndStatus(
                productId,
                orderStatus
        ))
                .thenReturn(Optional.of(order));
    }

    private void setUpOrders(
            List<OrderStatus> orderStatusList,
            List<Product> productList
    ) {
        for (Product product : productList) {
            Long productId = product.getId();

            for (OrderStatus orderStatus : orderStatusList) {
                Order order = createOrder(product, buyer);
                order.setStatus(orderStatus);

                when(paymentService.findByProductIdAndStatus(
                        eq(productId),
                        eq(orderStatus)
                ))
                        .thenReturn(Optional.of(order));
            }
        }
    }

    private void setUpNegotiations(
            List<NegotiationStatus> negotiationStatusList,
            List<Product> productList
    ) {
        for (Product product : productList) {
            List<Nego> negoList = new ArrayList<>();

            for (NegotiationStatus negotiationStatus : negotiationStatusList) {
                Nego nego = createNego(product, buyer);
                nego.setStatus(negotiationStatus);

                negoList.add(nego);
            }

            when(negoService.findByStatusInAndProduct(
                    eq(negotiationStatusList),
                    eq(product)
            ))
                    .thenReturn(negoList);
        }
    }

    private void setUpChatRoom(Product soldOutProduct) {
        ChatRoom chatRoom = createChatRoom(soldOutProduct, buyer);
        chatRoom.setId(chatRoomId);

        when(chatService.getChatRoomByBuyerIdAndProductId(
                buyerId,
                soldOutProductId
        ))
                .thenReturn(chatRoom);

        when(chatService.getFirstChatUpdatedAt(
                chatRoomId,
                soldOutProductId
        ))
                .thenReturn(LocalDateTime.now());
    }

    private void setUpChatRooms(List<Product> productList) {
        for (Product product : productList) {
            Long productId = product.getId();

            chatRoomId++;
            ChatRoom chatRoom = createChatRoom(product, buyer);
            chatRoom.setId(chatRoomId);

            when(chatService.getChatRoomByBuyerIdAndProductId(
                    eq(buyerId),
                    eq(productId)
            ))
                    .thenReturn(chatRoom);

            when(chatService.getFirstChatUpdatedAt(
                    eq(chatRoomId),
                    eq(buyerId)
            ))
                    .thenReturn(LocalDateTime.now());
        }
    }
}