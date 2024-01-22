package site.goldenticket.domain.payment.dto.response;

import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.model.Payment;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record PurchaseCompletedDetailResponse(
        Long productId,
        String accommodationImage,
        String accommodationName,
        String roomName,
        ReservationType reservationType,
        Integer standardNumber,
        Integer maximumNumber,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer price,
        String buyerName,
        String buyerPhoneNumber,
        String buyerEmail,
        LocalDateTime completedDate,
        Long chatRoomId,
        String receiverNickname,
        String receiverProfileImage,
        LocalDateTime lastUpdatedAt
) {
    public static PurchaseCompletedDetailResponse create(Product product, Order order, Payment payment, User seller,Long chatRoomId, LocalDateTime lastUpdatedAt) {
        return new PurchaseCompletedDetailResponse(
                product.getId(),
                product.getAccommodationImage(),
                product.getAccommodationName(),
                product.getRoomName(),
                product.getReservationType(),
                product.getStandardNumber(),
                product.getMaximumNumber(),
                product.getCheckInTime(),
                product.getCheckOutTime(),
                product.getCheckInDate(),
                product.getCheckOutDate(),
                order.getPrice(),
                payment.getBuyerName(),
                payment.getBuyerTel(),
                payment.getBuyerEmail(),
                order.getUpdatedAt(),
                chatRoomId,
                seller.getNickname(),
                seller.getImageUrl(),
                lastUpdatedAt
        );

    }
}
