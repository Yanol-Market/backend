package site.goldenticket.domain.chat.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import site.goldenticket.domain.product.constants.ProductStatus;

@Builder
public record ChatRoomInfoResponse(
    Long chatRoomId,
    Long sellerId,
    Long buyerId,
    Long receiverId,
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
    Integer price,
    String chatStatus,
    Long negoId,
    Boolean negoAvailable
) {

}
