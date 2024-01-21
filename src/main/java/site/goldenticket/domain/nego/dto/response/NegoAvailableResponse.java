package site.goldenticket.domain.nego.dto.response;

import lombok.Builder;

@Builder
public record NegoAvailableResponse(
    Boolean negoAvailable,
    Long chatRoomId
) {

}
