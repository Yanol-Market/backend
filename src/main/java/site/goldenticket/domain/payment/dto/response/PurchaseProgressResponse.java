package site.goldenticket.domain.payment.dto.response;

import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;
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
    public static PurchaseProgressResponse create(Product product, String status, User seller,Long chatRoomId, Integer price, LocalDateTime lastUpdatedAt) {
        return new PurchaseProgressResponse(
                product.getId(),
                product.getAccommodationImage(),
                product.getAccommodationName(),
                product.getReservationType(),
                product.getRoomName(),
                product.getStandardNumber(),
                product.getMaximumNumber(),
                product.getCheckInTime(),
                product.getCheckOutTime(),
                product.getCheckInDate(),
                product.getCheckOutDate(),
                product.getGoldenPrice(),
                status,
                chatRoomId,
                seller.getNickname(),
                seller.getImageUrl(),
                price,
                lastUpdatedAt
        );
    }
}
