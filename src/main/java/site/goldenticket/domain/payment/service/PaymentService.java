package site.goldenticket.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
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
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.service.UserService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final IamportRepository iamportRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentCancelDetailRepository paymentCancelDetailRepository;
    private final UserService userService;
    private final NegoService negoService;
    private final ProductService productService;

    public PaymentDetailResponse getPaymentDetail(Long productId, PrincipalDetails principalDetails) {
        User user = userService.getUser(principalDetails.getUserId());

        //TODO: productId,userId 이용하여 해당 유저가 네고를 진행 하였는지 확인, 진행하였다면 상품 가격 바꾸기
        int price = 1000;


        //TODO: productId로 상품 테이블에서 상품 가져오기
        Product product = Product.builder().build();
        productService.getProduct(productId);
        if (product.isNotOnSale()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }

        return PaymentDetailResponse.of(user, product, price);
    }

    //    결제 테이블에 결제 정보 검증하고 사전에 결제 정보 저장
    public PaymentReadyResponse preparePayment(Long productId, PrincipalDetails principalDetails) {
        //TODO: 유저 id 가져오기, 해당 유저 유효성 검사
        User user = User.builder().build();

        //TODO: productId,userId 이용하여 해당 유저가 네고를 진행 하였는지 확인, 진행하였다면 상품 가격 바꾸기
        int price = 1000;

        //TODO: productId로 상품 테이블에서 상품 가져오기
        Product product = Product.builder().build();
        if (product.isNotOnSale()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }

        Order order = Order.of(product.getId(), user.getId(), price);
        Order savedOrder = orderRepository.save(order);

        iamportRepository.prepare(savedOrder.getId(), BigDecimal.valueOf(savedOrder.getTotalPrice()));
        return PaymentReadyResponse.create(user, product, savedOrder);
    }

    public PaymentResponse savePayment(PaymentRequest request, PrincipalDetails principalDetails) {
        Payment payment = iamportRepository.findPaymentByImpUid(request.getImpUid())
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (payment.isDifferentAmount(order.getPrice())) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT_ERROR);
        }

        Payment saved = paymentRepository.save(payment);

        if (payment.isNotPaid()) {
            //TODO: 네고한 사람인지 확인, 만료시간&결제완료 시각 확인
            //만약 만료시간이 지낫다면, 상품상태: 예약중 -> 판매중, 네고상태: 결제 대기중 -> 시간초과, 주문 상태: 결제 요청 -> 결제 실패
            return PaymentResponse.failed();
        }

        //TODO: 네고한 사람인지 확인, 만료시간&결제완료 시각 확인
        //만약 만료시간 지낫다면, 상품상태: 예약중 -> 판매중, 네고상태: 결제 대기중 -> 시간초과, 주문 상태: 결제 요청 -> 주문 실패, 결제 취소 로직 필요
        List<PaymentCancelDetail> paymentCancelDetails = iamportRepository.cancelPaymentByImpUid(request.getImpUid());
        paymentCancelDetailRepository.saveAll(paymentCancelDetails);
        //TODO: 네고 상태값 네고 종료로 변경
        order.waitTransfer();
        //TODO: 상품 상태를 예약중으로 업데이트(ProductService 사용 예정)
        return PaymentResponse.success();
    }
}
