package site.goldenticket.common.security.authentication.dto;

import lombok.Builder;
import site.goldenticket.common.security.authentication.token.dto.Token;

@Builder
public record AuthenticationToken(
        String grantType,
        String refreshToken,
        Long expiresIn,
        String accessToken
) {

    public static AuthenticationToken of(Token token) {
        return AuthenticationToken.builder()
                .grantType(token.grantType())
                .refreshToken(token.refreshToken())
                .accessToken(token.accessToken())
                .expiresIn(token.accessTokenExpired())
                .build();
    }
}
