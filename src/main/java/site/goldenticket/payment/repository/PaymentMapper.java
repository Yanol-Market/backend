package site.goldenticket.payment.repository;

import org.springframework.stereotype.Component;
import site.goldenticket.payment.model.Payment;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class PaymentMapper {
    public Payment mapFrom(com.siot.IamportRestClient.response.Payment payment) {
        return Payment.builder()
                .impUid(payment.getImpUid())
                .orderId(payment.getMerchantUid())
                .paymentMethod(payment.getPayMethod())
                .pgTid(payment.getPgTid())
                .escrow(payment.isEscrow())
                .applyNum(payment.getApplyNum())
                .bankCode(payment.getBankCode())
                .bankName(payment.getBankName())
                .cardCode(payment.getCardCode())
                .cardName(payment.getCardName())
                .cardNumber(payment.getCardNumber())
                .cardQuota(payment.getCardQuota())
                .name(payment.getName())
                .amount(payment.getAmount().intValue())
                .buyerName(payment.getBuyerName())
                .buyerEmail(payment.getBuyerEmail())
                .buyerTel(payment.getBuyerTel())
                .status(payment.getStatus())
                .startedAt(convertUnixToLocalDateTime(payment.getStartedAt()))
                .paidAt(convertDatetoLocalDate(payment.getPaidAt()))
                .failedAt(convertDatetoLocalDate(payment.getFailedAt()))
                .failReason(payment.getFailReason())
                .receiptUrl(payment.getReceiptUrl())
                .cashReceiptIssued(payment.isCashReceiptIssued())
                .build();
    }

    public static LocalDateTime convertUnixToLocalDateTime(long unixTimestamp) {
        // Instant를 사용하여 유닉스 타임스탬프를 LocalDateTime으로 변환
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDate convertDatetoLocalDate(Date date) {
        // java.util.Date를 java.time.Instant로 변환
        Instant instant = date.toInstant();

        // java.time.Instant를 java.time.LocalDate로 변환
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
