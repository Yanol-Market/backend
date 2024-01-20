package site.goldenticket.domain.chat.dto.response;

import site.goldenticket.domain.chat.constants.ChatRoomStatus;

import java.time.LocalDateTime;

public record ProgressChatResponse(
        Long chatRoomId,
        String receiverNickname,
        String receiverProfileImage,
        int price,
        ChatRoomStatus chatRoomStatus,
        LocalDateTime lastUpdatedAt
) {
}
