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
    COMMON_RESOURCE_NOT_FOUND(NOT_FOUND, "존재하지 않는 리소스입니다."),
    COMMON_ENTITY_NOT_FOUND(BAD_REQUEST, "존재하지 않는 엔티티입니다."),
    COMMON_JSON_PROCESSING_ERROR(BAD_REQUEST, "Json 변환 중 오류"),

    //Payment,
    PRODUCT_NOT_ON_SALE(NOT_FOUND, "현재 판매중이지 않은 상품입니다"),
    IAMPORT_ERROR(INTERNAL_SERVER_ERROR, "PG사 연동 오류입니다"),
    INVALID_PAYMENT_AMOUNT_ERROR(CONFLICT, "주문금액과 실 결제금액이 다릅니다."),
    ORDER_NOT_FOUND(BAD_REQUEST, "주문 정보를 찾을 수 없습니다"),
    PAYMENT_NOT_FOUND(BAD_REQUEST, "결제 정보를 찾을 수 없습니다"),

    COMMON_CANNOT_NEGOTIATE(BAD_REQUEST, "더 이상 네고할 수 없습니다."),
    COMMON_NEGO_ALREADY_APPROVED(BAD_REQUEST, "승인된 네고는 가격 제안을 할 수 없습니다."),
    COMMON_CANNOT_CONFIRM_NEGO(BAD_REQUEST, "네고를 승인할수 없습니다."),
    COMMON_NEGO_APPROVAL_REQUIRED(BAD_REQUEST, "네고 승인이 필요합니다."),
    COMMON_ONLY_CAN_DENY_WHEN_NEGOTIATING(BAD_REQUEST, "네고 중인 경우에만 거절할 수 있습니다."),
    COMMON_NEGO_TIMEOUT(BAD_REQUEST, "20분이 지나 제안할수 없습니다"),
    COMMON_NEGO_ALREADY_NEGOTIATING(BAD_REQUEST, "이미 네고 중인 상품에 대해서는 가격 제안을 할 수 없습니다."),
    COMMON_CANNOT_HANDOVER(BAD_REQUEST, "양도가 불가능한 상태입니다."),
    NEGO_NOT_FOUND(NOT_FOUND, "네고 정보를 찾을 수 없습니다"),
    NEGO_NOT_COMPLETED(BAD_REQUEST, "Nego not in completed status."),


    // Auth
    EMPTY_EMAIL(BAD_REQUEST, "이메일은 필수 값 입니다."),
    EMPTY_PASSWORD(BAD_REQUEST, "비밀번호는 필수 값 입니다."),
    EMPTY_SUCCESS_HANDLER(INTERNAL_SERVER_ERROR, "SuccessHandler 필수 값 입니다."),
    EMPTY_FAILURE_HANDLER(INTERNAL_SERVER_ERROR, "FailureHandler 필수 값 입니다."),
    LOGIN_FAIL(BAD_REQUEST, "이메일, 비밀번호를 확인해주세요."),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰 입니다."),
    SAVE_REFRESH_TOKEN_FAILED(UNAUTHORIZED, "Token 저장 중 오류가 발생 했습니다."),
    EMPTY_REFRESH_TOKEN(BAD_REQUEST, "Refresh Token은 필수 값 입니다."),
    EMPTY_ACCESS_TOKEN(BAD_REQUEST, "Access Token은 필수 값 입니다."),
    LOGOUT_ACCESS_TOKEN(UNAUTHORIZED, "로그아웃 된 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰 입니다."),

    // User
    ALREADY_EXIST_EMAIL(BAD_REQUEST, "이미 사용중인 이메일입니다. 이미 가입하신 적이 있다면 로그인을 시도해주세요"),
    ALREADY_EXIST_NICKNAME(BAD_REQUEST, "이미 사용중인 아이디입니다."),
    USER_NOT_FOUND(NOT_FOUND, "유저 정보가 존재하지 않습니다"),
    NOT_FOUND_USER(BAD_REQUEST, "존재하지 않는 사용자입니다."),
    ALREADY_REGISTER_YANOLJA_ID(BAD_REQUEST, "이미 등록된 야놀자 아이디가 존재합니다."),

    // Reservation
    RESERVATION_NOT_FOUND(NOT_FOUND, "예약 정보가 존재하지 않습니다."),

    // Product
    PRODUCT_ALREADY_EXISTS(CONFLICT, "이미 등록된 상품이 존재합니다."),
    PRODUCT_NOT_FOUND(NOT_FOUND, "상품 정보가 존재하지 않습니다."),

    // Search
    SEARCH_HISTORY_NOT_FOUND(NOT_FOUND, "검색 이력이 존재하지 않습니다."),

    //Chat
    CHAT_ROOM_NOT_FOUND(NOT_FOUND, "존재하지 않는 채팅방입니다."),
    INVALID_USER_TYPE(BAD_REQUEST, "userType이 올바르지 않습니다. (userType: all, seller, buyer)"),
    INVALID_SENDER_TYPE(BAD_REQUEST, "SenderType이 올바르지 않습니다. (SenderType: SYSTEM, SELLER, BUYER)");

    private final HttpStatus httpStatus;
    private final String message;
}
