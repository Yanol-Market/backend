package site.goldenticket.domain.payment.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.payment.dto.response.PaymentDetailResponse;
import site.goldenticket.domain.payment.dto.response.PaymentReadyResponse;
import site.goldenticket.domain.payment.repository.IamportRepository;
import site.goldenticket.domain.payment.repository.OrderRepository;
import site.goldenticket.domain.payment.repository.PaymentRepository;
import site.goldenticket.domain.payment.dto.request.PaymentRequest;
import site.goldenticket.domain.payment.dto.response.PaymentResponse;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final IamportRepository iamportRepository;
    private final PaymentRepository paymentRepository;
//    private final UserService userService;
//    private final NegoService negoService;
//    private final ProductService productService;

    public PaymentDetailResponse getPaymentDetail(Long productId) {
        //TODO: 유저 id 가져오기, 해당 유저 유효성 검사
        User user = new User();

        //TODO: productId,userId 이용하여 해당 유저가 네고를 진행 하였는지 확인, 진행하였다면 상품 가격 바꾸기
        int price = 1000;

        //TODO: productId로 상품 테이블에서 상품 가져오기
        Product product = new Product();
        if (product.isNotOnSale()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }

        return PaymentDetailResponse.create(user, product, price);
    }

//    결제 테이블에 결제 정보 검증하고 사전에 결제 정보 저장
    public PaymentReadyResponse preparePayment(Long productId) {
        //TODO: 유저 id 가져오기, 해당 유저 유효성 검사
        User user = new User();

        //TODO: productId,userId 이용하여 해당 유저가 네고를 진행 하였는지 확인, 진행하였다면 상품 가격 바꾸기
        Integer price = 100;

        //TODO: productId로 상품 테이블에서 상품 가져오기
        Product product = new Product();
        if (product.isNotOnSale()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }

        Order order = Order.builder()
                .productId(product.getProductId())
                .userId(user.getId())
                .status(OrderStatus.REQUEST_PAYMENT)
                .price(price)
                .build();

        Order savedOrder = orderRepository.save(order);

        //TODO: paymentService로 분리
        iamportRepository.prepare(savedOrder.getId(), BigDecimal.valueOf(savedOrder.getTotalPrice()));
        return PaymentReadyResponse.create(user, product, savedOrder);
    }

    public PaymentResponse savePayment(PaymentRequest request) {
        Payment payment = iamportRepository.findPaymentByImpUid(request.getImpUid());
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow();

        if (!payment.getAmount().equals(order.getPrice())) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT_ERROR);
        }

        Payment saved = paymentRepository.save(payment);

        if (!payment.isPaid()) {
            //TODO: 네고한 사람인지 확인, 만료시간&결제완료 시각 확인
            //만약 만료시간이 지낫다면, 상품상태: 예약중 -> 판매중, 네고상태: 결제 대기중 -> 시간초과, 주문 상태: 결제 요청 -> 결제 실패
            return new PaymentResponse(PaymentResponse.PaymentResult.FAILED);
        }

        //TODO: 네고한 사람인지 확인, 만료시간&결제완료 시각 확인
        //만약 만료시간 지낫다면, 상품상태: 예약중 -> 판매중, 네고상태: 결제 대기중 -> 시간초과, 주문 상태: 결제 요청 -> 주문 실패, 결제 취소 로직 필요

        //TODO: 네고 상태값 네고 종료로 변경
        Order savedOrder = orderRepository.findById(saved.getOrderId()).orElseThrow();
        savedOrder.updateStatus(OrderStatus.WAITING_TRANSFER);
        //TODO: 상품 상태를 예약중으로 업데이트(ProductService 사용 예정)
        return new PaymentResponse(PaymentResponse.PaymentResult.SUCCESS);
    }

    @Getter
    public static class Product {
        private Long productId = 100L;
        private Long userId = 101L;
        private String imageUrl = "default-image-url.jpg";
        private String accommodationName = "Default Accommodation";
        private String roomName = "Default Room";
        private String reservationType = "숙박";
        private Integer standardNumber = 1;
        private Integer maximumNumber = 2;
        private Integer goldenPrice = 100;
        private LocalDate checkInDate = LocalDate.now();
        private LocalTime checkInTime = LocalTime.now();
        private LocalDate checkOutDate = LocalDate.now().plusDays(1);
        private LocalTime checkOutTime = LocalTime.now().plusHours(1);
        private String status = "판매중";

        public boolean isOnSale() {
            return this.status.equals("판매중");
        }

        public boolean isNotOnSale() {
            return !isOnSale();
        }
    }

    @Getter
    public static class User {
        private Long id = 1L;
        private String name = "test";
        private String phoneNumber = "010-1234-5678";
        private String email = "test@mail";
    }
}
