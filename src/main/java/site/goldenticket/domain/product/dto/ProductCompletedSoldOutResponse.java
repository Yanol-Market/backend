package site.goldenticket.domain.product.dto;

import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.util.DiscountCalculatorUtil;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ProductCompletedSoldOutResponse(
        Long productId,
        String accommodationImage,
        String accommodationName,
        ReservationType reservationType,
        String roomName,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int goldenPrice,
        LocalDateTime completedDate,
        LocalDateTime calculatedDate,
        int fee,
        int calculatedPrice,
        Long chatRoomId,
        String receiverNickname,
        String receiverProfileImage,
        LocalDateTime lastUpdatedAt

) {
    public static ProductCompletedSoldOutResponse fromEntity(Product product, Order order, User user, ChatRoom chatRoom, LocalDateTime lastUpdatedAt) {

        int goldenPrice = product.getGoldenPrice();
        int fee = DiscountCalculatorUtil.calculateFee(goldenPrice);
        int calculatedPrice = goldenPrice - fee;

        return new ProductCompletedSoldOutResponse(
                product.getId(),
                product.getAccommodationImage(),
                product.getAccommodationName(),
                product.getReservationType(),
                product.getRoomName(),
                product.getCheckInTime(),
                product.getCheckOutTime(),
                product.getCheckInDate(),
                product.getCheckOutDate(),
                product.getGoldenPrice(),
                order.getUpdatedAt(),
                order.getUpdatedAt(),
                fee,
                calculatedPrice,
                chatRoom.getId(),
                user.getNickname(),
                user.getImageUrl(),
                lastUpdatedAt
        );
    }
}
