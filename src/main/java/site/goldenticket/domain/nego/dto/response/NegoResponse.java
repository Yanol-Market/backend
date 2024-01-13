package site.goldenticket.domain.nego.dto.response;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class NegoResponse {
    private Boolean consent;
    private LocalDateTime expirationTime;
    private NegotiationStatus negotiationStatus;

    public static NegoResponse fromEntity(Nego nego){
        return NegoResponse.builder()
                .consent(nego.getConsent())
                .expirationTime(nego.getExpirationTime())
                .negotiationStatus(nego.getStatus())
                .build();
    }

}