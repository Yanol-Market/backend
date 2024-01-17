package site.goldenticket.domain.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlertRequest(
    @NotNull(message = "회원 ID를 입력해주세요.")
    Long userId,
    @NotBlank(message = "알림 내용을 입력해주세요.")
    String content
) {

}
