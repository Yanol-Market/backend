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

    // Reservation
    RESERVATION_NOT_FOUND(NOT_FOUND, "예약 정보가 존재하지 않습니다."),

    // Product
    PRODUCT_ALREADY_EXISTS(CONFLICT, "이미 등록된 상품이 존재합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
