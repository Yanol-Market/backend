package site.goldenticket.domain.chat.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import site.goldenticket.domain.product.constants.ProductStatus;

@Builder
public record ChatRoomInfoResponse(
    Long chatRoomId,
    String receiverNickname,
    String receiverProfileImage,
    Long productId,
    String accommodationName,
    String roomName,
    String accommodationImage,
    LocalDate checkInDate,
    LocalDate checkOutDate,
    LocalTime checkInTime,
    LocalTime checkOutTime,
    ProductStatus productStatus,
    Integer price
) {

}
