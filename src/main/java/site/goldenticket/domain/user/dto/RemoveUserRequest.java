package site.goldenticket.domain.user.dto;

import site.goldenticket.domain.user.entity.DeleteReason;

public record RemoveUserRequest(
        String reason
) {

    public DeleteReason toEntity() {
        return DeleteReason.builder()
                .reason(reason)
                .build();
    }
}
