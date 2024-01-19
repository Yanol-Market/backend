package site.goldenticket.domain.user.dto;

import lombok.Builder;
import site.goldenticket.domain.user.entity.User;

@Builder
public record UserResponse(
        Long id,
        String email,
        String name,
        String nickname,
        String imageUrl,
        String phoneNumber,
        Long yanoljaId
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .phoneNumber(user.getPhoneNumber())
                .yanoljaId(user.getYanoljaId())
                .build();
    }
}
