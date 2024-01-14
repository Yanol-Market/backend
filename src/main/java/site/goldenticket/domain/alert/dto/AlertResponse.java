package site.goldenticket.domain.alert.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AlertResponse(
    Long alertId,
    String content,
    Boolean viewed,
    LocalDateTime createdAt
) {

}
