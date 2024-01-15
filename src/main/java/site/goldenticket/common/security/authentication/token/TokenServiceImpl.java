package site.goldenticket.common.security.authentication.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.common.security.authentication.dto.AuthenticationToken;
import site.goldenticket.common.security.authentication.token.dto.Token;
import site.goldenticket.common.security.exception.SaveTokenException;

import static site.goldenticket.common.response.ErrorCode.SAVE_REFRESH_TOKEN_FAILED;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Override
    public AuthenticationToken generatedToken(String randomToken, String email) {
        Token token = tokenProvider.generateToken(randomToken, email);

        try {
            redisService.set(randomToken, email, token.refreshTokenExpired());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SaveTokenException(SAVE_REFRESH_TOKEN_FAILED);
        }

        return AuthenticationToken.of(token);
    }

    @Override
    public void removeRefreshToken(String refreshToken) {
        String randomToken = tokenProvider.getSubject(refreshToken);
        log.info("삭제 RefreshToken randomToken = [{}]", randomToken);

        if (redisService.delete(randomToken)) {
            log.info("[{}} RefreshToken 삭제 성공", randomToken);
        }
    }
}
