package site.goldenticket.common.security.authentication.token.dto;

import lombok.Builder;

@Builder
public record Token(
        String grantType,
        String refreshToken,
        Long refreshTokenExpired,
        String accessToken,
        Long accessTokenExpired
) {
}
