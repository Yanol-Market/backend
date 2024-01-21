package site.goldenticket.domain.user.dto;

import lombok.Builder;
import site.goldenticket.domain.user.entity.User;

@Builder
public record AccountResponse(
        String name,
        String bankName,
        String accountNumber
) {

    public static AccountResponse from(User user) {
        return AccountResponse.builder()
                .name(user.getName())
                .bankName(user.getBankName())
                .accountNumber(user.getAccountNumber())
                .build();
    }
}
