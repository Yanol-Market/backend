package site.goldenticket.common.security.authentication.dto;

import org.springframework.util.StringUtils;
import site.goldenticket.common.security.exception.InvalidAuthenticationArgumentException;

import static site.goldenticket.common.response.ErrorCode.EMPTY_EMAIL;
import static site.goldenticket.common.response.ErrorCode.EMPTY_PASSWORD;

public record LoginRequest(String email, String password) {

    public LoginRequest {
        if (!StringUtils.hasText(email)) {
            throw new InvalidAuthenticationArgumentException(EMPTY_EMAIL);
        }

        if (!StringUtils.hasText(password)) {
            throw new InvalidAuthenticationArgumentException(EMPTY_PASSWORD);
        }
    }
}
