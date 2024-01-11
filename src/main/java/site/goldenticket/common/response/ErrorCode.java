package site.goldenticket.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    COMMON_SYSTEM_ERROR(INTERNAL_SERVER_ERROR, "시스템 오류입니다."),
    COMMON_INVALID_PARAMETER(BAD_REQUEST, "요청한 값이 올바르지 않습니다."),
    COMMON_ENTITY_NOT_FOUND(BAD_REQUEST, "존재하지 않는 엔티티입니다."),
    PRODUCT_NOT_ON_SALE(NOT_FOUND, "현재 판매중이지 않은 상품입니다"),
    IAMPORT_ERROR(INTERNAL_SERVER_ERROR, "PG사 연동 오류입니다")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
