package site.goldenticket.common.security.exception;

import org.springframework.security.core.AuthenticationException;
import site.goldenticket.common.response.ErrorCode;

public class InvalidAuthenticationArgumentException extends AuthenticationException {

    public InvalidAuthenticationArgumentException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
