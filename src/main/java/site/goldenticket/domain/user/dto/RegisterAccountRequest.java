package site.goldenticket.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterAccountRequest(
        @NotBlank(message = "은행명을 선택해주세요.")
        String bankName,
        @NotBlank(message = "계좌번호를 입력해주세요.")
        String accountNumber
) {
}
