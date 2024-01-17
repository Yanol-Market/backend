package site.goldenticket.domain.chat.dto;

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
    ProductStatus productStatus,
    Integer price
) {

}
