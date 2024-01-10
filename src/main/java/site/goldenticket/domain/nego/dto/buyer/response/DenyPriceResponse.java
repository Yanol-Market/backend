package site.goldenticket.domain.nego.dto.buyer.response;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.nego.entity.Nego;

@Getter
@Builder
public class DenyPriceResponse {
    private Boolean consent;

    public static DenyPriceResponse fromEntity(Nego nego){
        return DenyPriceResponse.builder()
                .consent(nego.getConsent())
                .build();
    }
}
