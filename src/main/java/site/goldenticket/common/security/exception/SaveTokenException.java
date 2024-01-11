package site.goldenticket.common.security.exception;

import org.springframework.security.core.AuthenticationException;
import site.goldenticket.common.response.ErrorCode;

public class SaveTokenException extends AuthenticationException {

    public SaveTokenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
