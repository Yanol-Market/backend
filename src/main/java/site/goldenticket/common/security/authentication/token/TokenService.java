package site.goldenticket.common.security.authentication.token;

import site.goldenticket.common.security.authentication.dto.AuthenticationToken;

public interface TokenService {

    AuthenticationToken generatedToken(String randomToken, String username);

    String getUsername(String token);

    void removeRefreshToken(String refreshToken);

    void addBlackList(String accessToken);

    boolean isBlackListToken(String token);
}
