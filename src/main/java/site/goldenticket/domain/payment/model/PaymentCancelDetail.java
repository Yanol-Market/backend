package site.goldenticket.domain.payment.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class PaymentCancelDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pgTid;
    private Integer amount;
    private LocalDateTime cancelledAt;
    private String reason;
    private String receiptUrl;

    @Builder
    public PaymentCancelDetail(String pgTid, Integer amount, LocalDateTime cancelledAt, String reason, String receiptUrl) {
        this.pgTid = pgTid;
        this.amount = amount;
        this.cancelledAt = cancelledAt;
        this.reason = reason;
        this.receiptUrl = receiptUrl;
    }
}
