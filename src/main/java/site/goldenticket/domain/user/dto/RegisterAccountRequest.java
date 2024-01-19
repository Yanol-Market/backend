package site.goldenticket.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterAccountRequest(
        @NotBlank
        String bankName,
        @NotBlank
        String accountNumber
) {
}
