package site.goldenticket.domain.security.dto;

import lombok.Builder;
import site.goldenticket.common.security.authentication.dto.AuthenticationToken;

@Builder
public record YanoljaLoginResponse(
        boolean isFirst,
        AuthenticationToken token,
        YanoljaUserResponse userInfo
) {
}
