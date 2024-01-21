package site.goldenticket.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotEmpty(message = "현재 비밀번호를 입력해 주세요.")
        String originPassword,
        @NotEmpty(message = "변경할 비밀번호를 입력해 주세요.")
        @Size(min = 6, max = 20, message = "비밀번호는 6자 이상, 20자 이하여야 합니다.")
        String changePassword
) {
}
