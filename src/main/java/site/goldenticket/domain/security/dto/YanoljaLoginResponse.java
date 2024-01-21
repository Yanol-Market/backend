package site.goldenticket.domain.security.dto;

import lombok.Builder;
import site.goldenticket.common.security.authentication.dto.AuthenticationToken;

@Builder
public record YanoljaLoginResponse(
        boolean isFirst,
        AuthenticationToken token,
        YanoljaUserResponse userInfo
) {

    public static YanoljaLoginResponse firstLoginUser(YanoljaUserResponse yanoljaUser) {
        return YanoljaLoginResponse.builder()
                .isFirst(true)
                .userInfo(yanoljaUser)
                .build();
    }

    public static YanoljaLoginResponse loginUser(
            YanoljaUserResponse yanoljaUser,
            AuthenticationToken token
    ) {
        return YanoljaLoginResponse.builder()
                .isFirst(true)
                .userInfo(yanoljaUser)
                .token(token)
                .build();
    }
}
