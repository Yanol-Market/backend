package site.goldenticket.payment.dto.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PaymentResponse {
    //결제 성공 -> 아임포트 측에서 결제 성공
    //시간 초과 -> 아임포트 측에서 결제 성공이지만 결제 완료 시간이 만료 시간보다 늦을때, 결제 취소
    //결제 실패 -> 아임포트 측에서 결제 실패라고 뜨는것
    private PaymentResult result;

    public enum PaymentResult{
        SUCCESS, TIME_OVER, FAILED
    }
}
