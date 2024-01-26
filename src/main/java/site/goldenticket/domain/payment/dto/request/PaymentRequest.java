package site.goldenticket.domain.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {
    @NotNull
    private String impUid;
    @NotNull
    private Long orderId;
}
