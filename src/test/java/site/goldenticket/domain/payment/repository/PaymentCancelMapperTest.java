package site.goldenticket.domain.payment.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.siot.IamportRestClient.response.PaymentCancelDetail;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentCancelMapperTest {

    Gson gson = new Gson();
    PaymentCancelMapper mapper = new PaymentCancelMapper();

    @Test
    void mapFrom() throws Exception {
        String target = """
                [
                      {
                        "pg_tid": "StdpayCARDINIBillTst20240125192245810285",
                        "amount": 206,
                        "cancelled_at": 1706178259,
                        "reason": "취소요청api",
                        "receipt_url": "https://iniweb.inicis.com/receipt/iniReceipt.jsp?noTid=StdpayCARDINIBillTst20240125192245810285",
                        "cancellation_id": "YP4JND7CV71752BJ3FFG"
                      }
                    ]""";
        PaymentCancelDetail[] paymentCancelDetails = gson.fromJson(target, TypeToken.get(PaymentCancelDetail[].class));

        List<site.goldenticket.domain.payment.model.PaymentCancelDetail> results = mapper.mapFrom(paymentCancelDetails);

        assertThat(results.get(0).getReason()).isEqualTo("취소요청api");
        assertThat(results.get(0).getAmount()).isEqualTo(206);

    }


}