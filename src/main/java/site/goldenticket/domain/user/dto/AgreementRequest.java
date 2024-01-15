package site.goldenticket.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import site.goldenticket.domain.user.entity.Agreement;
import site.goldenticket.domain.user.entity.User;

public record AgreementRequest(
        @NotEmpty(message = "마케팅 동의는 필수 선택 사항입니다.")
        Boolean isMarketing
) {

    public Agreement toEntity(User user) {
        Agreement agreement = Agreement.builder()
                .marketing(isMarketing)
                .build();
        agreement.registerUser(user);
        return agreement;
    }
}
