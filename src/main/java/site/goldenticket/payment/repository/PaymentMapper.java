package site.goldenticket.payment.repository;

import org.springframework.stereotype.Component;
import site.goldenticket.payment.model.Payment;

import static site.goldenticket.common.utils.Converter.*;

@Component
public class PaymentMapper {
    public Payment mapFrom(com.siot.IamportRestClient.response.Payment payment) {
        return Payment.builder()
                .impUid(payment.getImpUid())
                .orderId(Long.valueOf(payment.getMerchantUid()))
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
                .status(convertStatus(payment.getStatus()))
                .startedAt(convertUnixToLocalDateTime(payment.getStartedAt()))
                .paidAt(convertDatetoLocalDate(payment.getPaidAt()))
                .failedAt(convertDatetoLocalDate(payment.getFailedAt()))
                .failReason(payment.getFailReason())
                .receiptUrl(payment.getReceiptUrl())
                .cashReceiptIssued(payment.isCashReceiptIssued())
                .build();
    }
}
