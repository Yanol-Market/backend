package site.goldenticket.domain.nego.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NegotiationStatus {

    NEGOTIATING("네고중"),
    PAYMENT_PENDING("결제대기중"),
    NEGOTIATION_COMPLETED("네고 종료"),
    NEGOTIATION_CANCELLED("네고 취소"),
    NEGOTIATION_TIMEOUT("20분 타임아웃");


    private final String statusDescription;

}
