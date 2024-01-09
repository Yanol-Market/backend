package site.goldenticket.payment.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record PaymentDetailResponse(
        Long productId,
        String imageUrl,
        String accommodationName,
        String roomName,
        String reservationType, //enum 으로
        Integer standardNumber,
        Integer maximumNumber,
        LocalDate checkInDate,
        LocalTime checkInTime,
        LocalDate checkOutDate,
        LocalTime checkOutTime,
        String userName,
        String phoneNumber,
        String email,
        Integer price,
        Integer fee,
        Integer totalPrice
) {
    public static PaymentDetailResponse create(User user, Product product, int price) {

        return new PaymentDetailResponse(
                product.productId,
                product.imageUrl,
                product.accommodationName,
                product.roomName,
                product.reservationType,
                product.standardNumber,
                product.maximumNumber,
                product.checkInDate,
                product.checkInTime,
                product.checkOutDate,
                product.checkOutTime,
                user.name,
                user.phoneNumber,
                user.email,
                price,
                (int) (price * 0.035),
                (int) (price * 1.035)
        );
    }

    static class Product {
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

    static class User {
        private Long id = 1L;
        private String name = "test";
        private String phoneNumber = "010-1234-5678";
        private String email = "test@mail";
    }
}
