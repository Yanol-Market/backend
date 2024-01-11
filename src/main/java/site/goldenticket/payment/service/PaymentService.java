package site.goldenticket.payment.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.payment.dto.response.PaymentDetailResponse;
import site.goldenticket.payment.dto.response.PaymentReadyResponse;
import site.goldenticket.payment.model.Order;
import site.goldenticket.payment.model.Order.OrderStatus;
import site.goldenticket.payment.repository.IamportRepository;
import site.goldenticket.payment.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final IamportRepository iamportRepository;
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
        Integer price = 1000;

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

    @Getter
    public static class Product {
        private Long productId = 1L;
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
