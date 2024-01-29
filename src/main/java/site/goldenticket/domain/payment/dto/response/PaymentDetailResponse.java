package site.goldenticket.domain.payment.dto.response;

import lombok.Builder;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record PaymentDetailResponse(
        Long orderId,
        Long productId,
        String imageUrl,
        String accommodationName,
        String roomName,
        ReservationType reservationType, //enum 으로
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
        Integer totalPrice,
        Integer savingPrice
) {
    public static PaymentDetailResponse of(Long orderId, User user, Product product, int price, int yanoljaPrice) {

        return new PaymentDetailResponse(
                orderId,
                product.getId(),
                product.getAccommodationImage(),
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
                (int) (price * 0.035),
                (int) (price * 1.035),
                calculateSavingPrice(yanoljaPrice, (int) (price * 1.035))
        );
    }

    public static Integer calculateSavingPrice(Integer yanoljaPrice, Integer orderPrice) {
        return yanoljaPrice - orderPrice;
    }
}
