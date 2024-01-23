package site.goldenticket.domain.nego.dto.response;

import java.time.LocalDateTime;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;

public record NegoTestResponse(
    Long negoId,
    Long productId,
    Long userId,
    Integer price,
    Integer count,
    Boolean consent,
    NegotiationStatus negotiationStatus,
    LocalDateTime expirationTime,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static NegoTestResponse fromEntity(Nego nego) {
        return new NegoTestResponse(nego.getId(), nego.getProductId(), nego.getUser().getId(),
            nego.getPrice(), nego.getCount(), nego.getConsent(), nego.getStatus(),
            nego.getExpirationTime(), nego.getCreatedAt(), nego.getUpdatedAt());
    }

}
