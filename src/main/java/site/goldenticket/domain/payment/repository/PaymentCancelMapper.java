package site.goldenticket.domain.payment.repository;

import org.springframework.stereotype.Component;
import site.goldenticket.domain.payment.model.PaymentCancelDetail;

import java.util.Arrays;
import java.util.List;

import static site.goldenticket.common.utils.Converter.*;

@Component
public class PaymentCancelMapper {
    public List<PaymentCancelDetail> mapFrom(com.siot.IamportRestClient.response.PaymentCancelDetail[] paymentCancelDetails) {
        return Arrays.stream(paymentCancelDetails).map(paymentCancelDetail ->
                        PaymentCancelDetail.builder()
                                .pgTid(paymentCancelDetail.getPgTid())
                                .cancelledAt(convertUnixToLocalDateTime(paymentCancelDetail.getCancelledAt()))
                                .amount(paymentCancelDetail.getAmount().intValue())
                                .build())
                .toList();
    }
}
