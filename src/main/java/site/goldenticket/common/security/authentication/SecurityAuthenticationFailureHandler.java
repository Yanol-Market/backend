package site.goldenticket.common.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.common.security.exception.InvalidAuthenticationArgumentException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static site.goldenticket.common.response.ErrorCode.LOGIN_FAIL;

@Slf4j
@RequiredArgsConstructor
public class SecurityAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        HttpStatus httpStatus;
        CommonResponse<Void> commonResponse;

        if (exception instanceof InvalidAuthenticationArgumentException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            commonResponse = CommonResponse.fail(exception.getMessage());
        } else {
            httpStatus = HttpStatus.UNAUTHORIZED;
            if (exception instanceof BadCredentialsException) {
                commonResponse = CommonResponse.fail(LOGIN_FAIL.getMessage());
            } else {
                commonResponse = CommonResponse.fail(exception.getMessage());
            }
        }

        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), commonResponse);
    }
}
