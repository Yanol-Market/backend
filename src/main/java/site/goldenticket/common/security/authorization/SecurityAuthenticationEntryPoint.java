package site.goldenticket.common.security.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import site.goldenticket.common.response.CommonResponse;

import java.io.IOException;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static site.goldenticket.common.security.authorization.SecurityErrorCode.ERROR_KEY;

@Slf4j
@RequiredArgsConstructor
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        log.error("[ERROR] AuthenticationEntryPoint = {}", authException.getMessage());

        Object attribute = request.getAttribute(ERROR_KEY);
        if (Objects.isNull(attribute)) {
            sendErrorResponse(response, CommonResponse.fail(authException.getMessage()));
            return;
        }

        SecurityErrorCode code = (SecurityErrorCode) attribute;
        sendErrorResponse(response, CommonResponse.fail(code.getMessage(), code.getCode()));
    }

    private void sendErrorResponse(HttpServletResponse response, CommonResponse<?> commonResponse)
            throws IOException {
        response.setStatus(UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        objectMapper.writeValue(response.getWriter(), commonResponse);
    }
}
