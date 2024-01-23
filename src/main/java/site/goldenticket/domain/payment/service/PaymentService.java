package site.goldenticket.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.alert.service.AlertService;
import site.goldenticket.domain.chat.service.ChatService;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.service.NegoService;
import site.goldenticket.domain.payment.dto.request.PaymentRequest;
import site.goldenticket.domain.payment.dto.response.PaymentDetailResponse;
import site.goldenticket.domain.payment.dto.response.PaymentReadyResponse;
import site.goldenticket.domain.payment.dto.response.PaymentResponse;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.model.Payment;
import site.goldenticket.domain.payment.model.PaymentCancelDetail;
import site.goldenticket.domain.payment.repository.IamportRepository;
import site.goldenticket.domain.payment.repository.OrderRepository;
import site.goldenticket.domain.payment.repository.PaymentCancelDetailRepository;
import site.goldenticket.domain.payment.repository.PaymentRepository;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final OrderRepository orderRepository;
    private final IamportRepository iamportRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentCancelDetailRepository paymentCancelDetailRepository;
    private final UserService userService;
    private final NegoService negoService;
    private final ProductService productService;
    private final AlertService alertService;
    private final ChatService chatService;

    public PaymentDetailResponse getPaymentDetail(Long productId, PrincipalDetails principalDetails) {
        User user = userService.findById(principalDetails.getUserId());

        Product product = productService.getProduct(productId);
        if (product.isNotOnSale()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }

        int price = product.getGoldenPrice();

        Optional<Nego> nego = negoService.getNego(user.getId(), product.getId());

        Order order = Order.of(product.getId(), user.getId(), null, price);

        if (nego.isPresent()) {
            if (Boolean.TRUE.equals(nego.get().getConsent())) {
                price = nego.get().getPrice();
                order = Order.of(product.getId(), user.getId(), nego.get().getStatus(), price);
            }
        }

        if (product.getUserId().equals(user.getId())) {
            throw new CustomException(ErrorCode.PRODUCT_CANNOT_BE_PURCHASED);
        }

        Order savedOrder = orderRepository.save(order);

        return PaymentDetailResponse.of(savedOrder.getId(), user, product, price, product.getYanoljaPrice());
    }

    public PaymentReadyResponse preparePayment(Long orderId, PrincipalDetails principalDetails) {

        User user = userService.findById(principalDetails.getUserId());

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
        );

        Product product = productService.getProduct(order.getProductId());
        if (product.isNotOnSale()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }

        order.requestPayment();

        iamportRepository.prepare(order.getId(), BigDecimal.valueOf(order.getTotalPrice()));
        return PaymentReadyResponse.create(user, product, order);
    }

    public PaymentResponse savePayment(PaymentRequest request, PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();

        Payment payment = iamportRepository.findPaymentByImpUid(request.getImpUid())
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        Product product = productService.getProduct(order.getProductId());

        Payment savedPayment = paymentRepository.save(payment);

        if (payment.isDifferentAmount(order.getTotalPrice())) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT_ERROR);
        }

        if (savedPayment.isNotPaid()) {
            order.paymentFailed();
            return PaymentResponse.failed();
        }

        if (order.getNegoStatus() != null) {
            Nego nego = negoService.getNego(userId, order.getProductId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NEGO_NOT_FOUND)
            );
            if (nego.getStatus() != order.getNegoStatus()) {
                List<PaymentCancelDetail> paymentCancelDetails = iamportRepository.cancelPaymentByImpUid(request.getImpUid());
                paymentCancelDetailRepository.saveAll(paymentCancelDetails);
                return PaymentResponse.timeOver();
            }
            nego.completed();
        }

        order.waitTransfer();
        product.setProductStatus(ProductStatus.RESERVED);
        alertService.createAlert(product.getUserId(),
                product.getAccommodationName() + "(" + product.getRoomName() + ") "
                        + "상품이 결제완료되었습니다." + order.getUpdatedAt().plusHours(3)
                        + "까지 양도 신청을 완료해주세요. 양도 미신청 시, 자동 양도 신청됩니다.");
        //채팅방 생성
        if(!chatService.existsChatRoomByBuyerIdAndProductId(userId, product.getId())) {
            chatService.createChatRoom(userId, product.getId());
        }

        return PaymentResponse.success();
    }

    public List<Order> findByStatusAndProductId(OrderStatus orderStatus, Long productId) {
        return orderRepository.findByStatusAndProductId(orderStatus, productId);
    }

    public Order findByProductId(Long productId) {
        return orderRepository.findByProductId(productId);
    }
}
