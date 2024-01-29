package site.goldenticket.domain.nego.dto.response;

import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;

public record PayResponse(
        Long id,
        Long productId,
        Integer price,
        Long userId,
        NegotiationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PayResponse fromEntity(Nego nego) {
        return new PayResponse(
                nego.getId(),
                nego.getProductId(),
                nego.getPrice(),
                nego.getUser().getId(),
                nego.getStatus(),
                nego.getCreatedAt(),
                LocalDateTime.now()
        );
    }
}
