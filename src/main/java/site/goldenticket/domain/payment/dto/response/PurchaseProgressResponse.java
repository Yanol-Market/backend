package site.goldenticket.domain.payment.dto.response;

import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record PurchaseProgressResponse(
        Long productId,
        String accommodationImage,
        String accommodationName,
        ReservationType reservationType,
        String roomName,
        Integer standardNumber,
        Integer maximumNumber,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer goldenPrice,
        String status,
        Long chatRoomId,
        String receiverNickname,
        String receiverProfileImage,
        Integer price,
        LocalDateTime lastUpdatedAt
) {

}
