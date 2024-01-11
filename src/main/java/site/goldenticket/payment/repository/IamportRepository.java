package site.goldenticket.payment.repository;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.PrepareData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class IamportRepository {
    private final IamportClient iamportClient;

    public void prepare(Long orderId, BigDecimal price) {
        try {
            iamportClient.postPrepare(new PrepareData(String.valueOf(orderId), price));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.IAMPORT_ERROR);
        }
    }
}
