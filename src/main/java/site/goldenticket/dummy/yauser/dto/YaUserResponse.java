package site.goldenticket.dummy.yauser.dto;

import lombok.Builder;
import site.goldenticket.dummy.yauser.model.YaUser;

@Builder
public record YaUserResponse(
        Long id,
        String email,
        String name,
        String phoneNumber
) {

    public static YaUserResponse of(YaUser yaUser) {
        return YaUserResponse.builder()
                .id(yaUser.getId())
                .email(yaUser.getEmail())
                .name(yaUser.getName())
                .phoneNumber(yaUser.getPhoneNumber())
                .build();
    }
}
