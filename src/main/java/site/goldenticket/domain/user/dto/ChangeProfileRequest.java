package site.goldenticket.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ChangeProfileRequest(
        @NotEmpty(message = "닉네임은 필수 입력 항목입니다.")
        @Size(max = 15, message = "닉네임은 1글자 이상, 15자 이하여야 합니다.")
        String nickname
) {
}
