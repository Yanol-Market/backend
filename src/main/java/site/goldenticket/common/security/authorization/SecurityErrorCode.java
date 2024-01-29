package site.goldenticket.common.security.authorization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode {

    TOKEN_EXPIRED("0001", "만료된 토큰입니다."),
    ;

    public static final String ERROR_KEY = "tokenError";

    private final String code;
    private final String message;
}
