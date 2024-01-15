package site.goldenticket.common.security.authentication.token;

import site.goldenticket.common.security.authentication.dto.AuthenticationToken;

public interface TokenService {

    AuthenticationToken generatedToken(String randomToken, String username);

    void removeRefreshToken(String refreshToken);
}
