package site.goldenticket.domain.nego.dto.response;

import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;

public record NegoResponse(
        Boolean consent,
        LocalDateTime expirationTime,
        NegotiationStatus negotiationStatus
) {
    public static NegoResponse fromEntity(Nego nego) {
        return new NegoResponse(
                nego.getConsent(),
                nego.getExpirationTime(),
                nego.getStatus()
        );
    }
}
