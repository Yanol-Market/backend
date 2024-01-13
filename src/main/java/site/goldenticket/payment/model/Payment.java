package site.goldenticket.payment.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import site.goldenticket.common.constants.PaymentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String impUid;
    private String pgTid;
    private String paymentMethod;
    private boolean escrow;
    private String applyNum;
    private String bankCode;
    private String bankName;
    private String cardCode;
    private String cardName;
    private String cardNumber;
    private int cardQuota;
    private String name;
    private Integer amount;
    private String buyerName;
    private String buyerEmail;
    private String buyerTel;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private LocalDateTime startedAt;
    private LocalDate paidAt;
    private LocalDate failedAt;
    private String failReason;
    private String receiptUrl;
    private boolean cashReceiptIssued;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Payment(String impUid, Long orderId, String paymentMethod, String pgTid, boolean escrow, String applyNum, String bankCode, String bankName, String cardCode, String cardName, String cardNumber, int cardQuota, String name, Integer amount, String buyerName, String buyerEmail, String buyerTel, PaymentStatus status, LocalDateTime startedAt, LocalDate paidAt, LocalDate failedAt, String failReason, String receiptUrl, boolean cashReceiptIssued) {
        this.impUid = impUid;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.pgTid = pgTid;
        this.escrow = escrow;
        this.applyNum = applyNum;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.cardCode = cardCode;
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.cardQuota = cardQuota;
        this.name = name;
        this.amount = amount;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.buyerTel = buyerTel;
        this.status = status;
        this.startedAt = startedAt;
        this.paidAt = paidAt;
        this.failedAt = failedAt;
        this.failReason = failReason;
        this.receiptUrl = receiptUrl;
        this.cashReceiptIssued = cashReceiptIssued;
    }

    public boolean isPaid(){
        return status == PaymentStatus.PAID;
    }
}
