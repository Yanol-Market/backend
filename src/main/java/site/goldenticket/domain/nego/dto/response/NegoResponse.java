package site.goldenticket.domain.nego.dto.response;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.nego.entity.Nego;

import java.time.LocalDateTime;

@Getter
@Builder
public class NegoResponse {
    private Boolean consent;
    private LocalDateTime expirationTime;

    public static NegoResponse fromEntity(Nego nego){
        return NegoResponse.builder()
                .consent(nego.getConsent())
                .expirationTime(nego.getExpirationTime())
                .build();
    }

}