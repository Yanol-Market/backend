package site.goldenticket.domain.nego.dto.buyer.response;

import lombok.Getter;

@Getter
public class ConfirmPriceResponse {
    private String message;

    public ConfirmPriceResponse() {
    }

    public ConfirmPriceResponse(String message) {
        this.message = message;
    }
}