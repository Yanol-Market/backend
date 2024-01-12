package site.goldenticket.payment.repository;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.payment.model.Payment;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class IamportRepository {
    private final IamportClient iamportClient;
    private final PaymentMapper paymentMapper;

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
            //TODO: 결제 취소 결과 올때는 결제 취소 디테일에 저장, 지금은 결제 취소 건은 생각 안할거임~
            return paymentMapper.mapFrom(response.getResponse());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.IAMPORT_ERROR);
        }
    }
}
