package site.goldenticket.common.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.common.security.authentication.dto.AuthenticationToken;
import site.goldenticket.common.security.authentication.dto.Token;
import site.goldenticket.common.security.exception.SaveTokenException;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static site.goldenticket.common.response.ErrorCode.SAVE_REFRESH_TOKEN_FAILED;

@Slf4j
@RequiredArgsConstructor
public class SecurityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String email = authentication.getName();
        log.info("Authentication Name = {}", email);
        Token token = tokenProvider.generateToken(email);

        try {
            redisService.set(token.refreshToken(), email, token.refreshTokenExpired());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SaveTokenException(SAVE_REFRESH_TOKEN_FAILED);
        }

        sendResponse(response, token);
    }

    private void sendResponse(HttpServletResponse response, Token token) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        objectMapper.writeValue(response.getWriter(), CommonResponse.ok(createAuthenticationToken(token)));
    }

    private AuthenticationToken createAuthenticationToken(Token token) {
        return AuthenticationToken.builder()
                .grantType(token.grantType())
                .refreshToken(token.refreshToken())
                .accessToken(token.accessToken())
                .expiresIn(token.accessTokenExpired())
                .build();
    }
}
