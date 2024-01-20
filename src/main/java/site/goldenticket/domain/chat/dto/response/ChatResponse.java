package site.goldenticket.domain.chat.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import site.goldenticket.domain.chat.entity.SenderType;

@Builder
public record ChatResponse(
    Long chatId,
    SenderType senderType,
    Long userId,
    String content,
    Boolean viewed,
    LocalDateTime createdAt
) {

}
