package site.goldenticket.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import site.goldenticket.domain.chat.entity.SenderType;

@Builder
public record ChatResponse(
    Long chatId,
    SenderType senderType,
    Long userId,
    String content,
    LocalDateTime createdAt
) {

}
