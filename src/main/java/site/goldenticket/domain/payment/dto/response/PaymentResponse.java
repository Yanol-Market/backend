package site.goldenticket.domain.payment.dto.response;

import lombok.Getter;

public record PaymentResponse(
    PaymentResult result,
    Long chatRoomId
) {

    //결제 성공 -> 아임포트 측에서 결제 성공
    //시간 초과 -> 아임포트 측에서 결제 성공이지만 결제 완료 시간이 만료 시간보다 늦을때, 결제 취소
    //결제 실패 -> 아임포트 측에서 결제 실패라고 뜨는것
    @Getter
    public enum PaymentResult {
        SUCCESS, TIME_OVER, FAILED
    }

    public static PaymentResponse success(Long chatRoomId) {
        return new PaymentResponse(PaymentResult.SUCCESS, chatRoomId);
    }

    public static PaymentResponse timeOver() {
        return new PaymentResponse(PaymentResult.TIME_OVER, -1L);
    }

    public static PaymentResponse failed() {
        return new PaymentResponse(PaymentResult.FAILED, -1L);
    }
}
