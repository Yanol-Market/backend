package site.goldenticket.common.security.authentication.token;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.common.security.authentication.dto.AuthenticationToken;
import site.goldenticket.common.security.authentication.token.dto.Token;
import site.goldenticket.common.security.exception.InvalidJwtException;
import site.goldenticket.common.security.exception.SaveTokenException;

import static site.goldenticket.common.response.ErrorCode.INVALID_TOKEN;
import static site.goldenticket.common.response.ErrorCode.SAVE_REFRESH_TOKEN_FAILED;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    public static final String REDIS_PREFIX = "RefreshToken:";

    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Override
    public AuthenticationToken generatedToken(String randomToken, String email) {
        Token token = tokenProvider.generateToken(randomToken, email);

        try {
            redisService.set(REDIS_PREFIX + randomToken, email, token.refreshTokenExpired());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SaveTokenException(SAVE_REFRESH_TOKEN_FAILED);
        }

        return AuthenticationToken.of(token);
    }

    @Override
    public void removeRefreshToken(String refreshToken) {
        try {
            String randomToken = tokenProvider.getSubject(refreshToken);
            log.info("삭제 RefreshToken randomToken = [{}]", randomToken);

            if (redisService.delete(REDIS_PREFIX + randomToken)) {
                log.info("[{}] RefreshToken 삭제 성공", randomToken);
            }
        } catch (ExpiredJwtException e) {
            log.info("[{}] 이미 만료된 토큰입니다.", refreshToken);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new InvalidJwtException(INVALID_TOKEN, e);
        }
    }
}
