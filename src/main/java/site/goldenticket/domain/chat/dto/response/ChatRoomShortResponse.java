package site.goldenticket.domain.chat.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatRoomShortResponse(
    Long chatRoomId,
    String receiverNickname,
    String receiverProfileImage,
    String accommodationName,
    String roomName,
    String lastMessage,
    LocalDateTime lastMessageCreatedAt,
    Boolean viewed
) {

}
