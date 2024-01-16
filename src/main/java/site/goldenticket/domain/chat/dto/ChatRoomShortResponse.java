package site.goldenticket.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatRoomShortResponse(
    Long chatRoomId,
    String receiverNickname,
    String receiverProfileImage,
    String accommodationName,
    String roomName,
    Integer price,
    String lastMessage,
    LocalDateTime lastMessageCreatedAt
) {

}
