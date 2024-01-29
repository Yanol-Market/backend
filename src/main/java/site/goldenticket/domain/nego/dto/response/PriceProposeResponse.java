package site.goldenticket.domain.nego.dto.response;

import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;

public record PriceProposeResponse(
        Long id,
        Long productId,
        Integer price,
        Integer count,
        NegotiationStatus status,
        LocalDateTime createdAt,
        LocalDateTime expirationTime
) {
    public static PriceProposeResponse fromEntity(Nego nego) {
        return new PriceProposeResponse(
                nego.getId(),
                nego.getProductId(),
                nego.getPrice(),
                nego.getCount(),
                nego.getStatus(),
                nego.getCreatedAt(),
                nego.getExpirationTime()
        );
    }
}
