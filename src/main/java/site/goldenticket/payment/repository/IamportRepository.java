package site.goldenticket.payment.repository;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.payment.model.Payment;
import site.goldenticket.payment.model.PaymentCancelDetail;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class IamportRepository {
    private final IamportClient iamportClient;
    private final PaymentMapper paymentMapper;
    private final PaymentCancelMapper paymentCancelMapper;

    public void prepare(Long orderId, BigDecimal price) {
        try {
            iamportClient.postPrepare(new PrepareData(String.valueOf(orderId), price));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.IAMPORT_ERROR);
        }
    }

    public Payment findPaymentByImpUid(String impUid) {
        try {
            IamportResponse<com.siot.IamportRestClient.response.Payment> response =
                    iamportClient.paymentByImpUid(impUid);
            return paymentMapper.mapFrom(response.getResponse());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.IAMPORT_ERROR);
        }
    }

    public List<PaymentCancelDetail> findPaymentCancelByImpUid(String impUid) {
        try {
            IamportResponse<com.siot.IamportRestClient.response.Payment> response =
                    iamportClient.paymentByImpUid(impUid);
            return paymentCancelMapper.mapFrom(response.getResponse().getCancelHistory());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.IAMPORT_ERROR);
        }
    }

}
