package site.goldenticket.domain.nego.dto.buyer.response;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.nego.entity.Nego;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConfirmPriceResponse {
    private Boolean consent;
    private LocalDateTime expirationTime;

    public static ConfirmPriceResponse fromEntity(Nego nego){
        return ConfirmPriceResponse.builder()
                .consent(nego.getConsent())
                .expirationTime(nego.getExpirationTime())
                .build();
    }

}