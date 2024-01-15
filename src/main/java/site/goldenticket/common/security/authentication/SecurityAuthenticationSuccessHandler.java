package site.goldenticket.common.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.common.security.authentication.dto.AuthenticationToken;
import site.goldenticket.common.security.authentication.token.TokenService;

import java.io.IOException;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class SecurityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String email = authentication.getName();
        log.info("Authentication Name = {}", email);

        String randomToken = UUID.randomUUID().toString();
        AuthenticationToken authenticationToken = tokenService.generatedToken(randomToken, email);

        sendResponse(response, authenticationToken);
    }

    private void sendResponse(HttpServletResponse response, AuthenticationToken authenticationToken) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        objectMapper.writeValue(response.getWriter(), CommonResponse.ok(authenticationToken));
    }
}
