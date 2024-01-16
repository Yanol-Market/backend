package site.goldenticket.domain.alert.dto;

import lombok.Builder;

@Builder
public record AlertUnSeenResponse(
    Boolean existsNewAlert
) {

}
