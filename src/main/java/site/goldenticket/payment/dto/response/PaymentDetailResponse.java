package site.goldenticket.payment.dto.response;

import lombok.Builder;
import site.goldenticket.payment.service.PaymentService;

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
    public static PaymentDetailResponse create(PaymentService.User user, PaymentService.Product product, int price) {

        return new PaymentDetailResponse(
                product.getProductId(),
                product.getImageUrl(),
                product.getAccommodationName(),
                product.getRoomName(),
                product.getReservationType(),
                product.getStandardNumber(),
                product.getMaximumNumber(),
                product.getCheckInDate(),
                product.getCheckInTime(),
                product.getCheckOutDate(),
                product.getCheckOutTime(),
                user.getName(),
                user.getPhoneNumber(),
                user.getEmail(),
                price,
                (int) (price * 0.05),
                (int) (price * 1.05)
        );
    }
}
