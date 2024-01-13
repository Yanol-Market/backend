package site.goldenticket.domain.user.dto;

import site.goldenticket.domain.user.entity.User;

public record JoinResponse(Long id) {

    public static JoinResponse from(User user) {
        return new JoinResponse(user.getId());
    }
}
