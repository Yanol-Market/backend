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

}
