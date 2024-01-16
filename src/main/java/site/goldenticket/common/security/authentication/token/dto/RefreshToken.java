package site.goldenticket.common.security.authentication.token.dto;

import lombok.Builder;

@Builder
public record RefreshToken(
        String email,
        String refreshToken
) {
}
