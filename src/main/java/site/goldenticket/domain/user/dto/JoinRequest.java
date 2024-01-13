package site.goldenticket.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import site.goldenticket.domain.user.entity.User;

public record JoinRequest(
        @NotEmpty(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 2, message = "이름은 두 글자 이상의 한글이어야 합니다.")
        String name,
        @NotEmpty(message = "닉네임은 필수 입력 항목입니다.")
        @Size(max = 15, message = "닉네임은 1글자 이상, 15자 이하여야 합니다.")
        String nickname,
        @NotEmpty(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "이메일은 유효한 형식이어야 합니다.")
        String email,
        @NotEmpty(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 6, max = 20, message = "비밀번호는 6자 이상, 20자 이하여야 합니다.")
        String password,
        @NotEmpty(message = "휴대폰 번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "000-0000-0000 형식이여야 합니다.")
        String phoneNumber,
        Long yanoljaId,
        AgreementRequest agreement
) {

        public User toEntity(String encodePassword) {
                User user = User.builder()
                        .name(name)
                        .nickname(nickname)
                        .email(email)
                        .password(encodePassword)
                        .phoneNumber(phoneNumber)
                        .yanoljaId(yanoljaId)
                        .build();
                agreement.toEntity(user);
                return user;
        }
}
