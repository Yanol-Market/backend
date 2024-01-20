package site.goldenticket.domain.chat.dto.response;

import lombok.Builder;

@Builder
public record ChatRoomResponse(
    Long chatRoomId,
    Long userId,
    Long productId
) {

}
