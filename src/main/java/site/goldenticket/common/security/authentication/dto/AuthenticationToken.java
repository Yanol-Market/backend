package site.goldenticket.common.security.authentication.dto;

import lombok.Builder;

@Builder
public record AuthenticationToken(
        String grantType,
        String refreshToken,
        Long expiresIn,
        String accessToken
) {
}
