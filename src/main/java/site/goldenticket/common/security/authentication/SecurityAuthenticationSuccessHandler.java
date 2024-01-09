package site.goldenticket.common.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.common.security.authentication.dto.AuthenticationToken;
import site.goldenticket.common.security.authentication.dto.Token;

import java.io.IOException;

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
        sendResponse(response, token);
        redisService.set(token.refreshToken(), email, token.refreshTokenExpired());
    }

    private void sendResponse(HttpServletResponse response, Token token) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
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
