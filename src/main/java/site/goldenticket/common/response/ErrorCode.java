package site.goldenticket.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    COMMON_SYSTEM_ERROR(INTERNAL_SERVER_ERROR, "시스템 오류입니다."),
    COMMON_INVALID_PARAMETER(BAD_REQUEST, "요청한 값이 올바르지 않습니다."),
    COMMON_ENTITY_NOT_FOUND(BAD_REQUEST, "존재하지 않는 엔티티입니다."),
    COMMON_CANNOT_NEGOTIATE(BAD_REQUEST, "더 이상 네고할 수 없습니다."),
    COMMON_NEGO_ALREADY_APPROVED(BAD_REQUEST, "승인된 네고는 가격 제안을 할 수 없습니다."),
    COMMON_CANNOT_CONFIRM_NEGO(BAD_REQUEST,"네고를 승인할수 없습니다."),
    COMMON_NEGO_APPROVAL_REQUIRED(BAD_REQUEST, "네고 승인이 필요합니다."),
    COMMON_ONLY_CAN_DENY_WHEN_NEGOTIATING(BAD_REQUEST,"네고 중인 경우에만 거절할 수 있습니다."),
    COMMON_NEGO_TIMEOUT(BAD_REQUEST,"20분이 지나 제안할수 없습니다"),
    COMMON_NEGO_ALREADY_NEGOTIATING(BAD_REQUEST,"이미 네고 중인 상품에 대해서는 가격 제안을 할 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
