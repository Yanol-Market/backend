package site.goldenticket.domain.user.wish.dto;

import lombok.Builder;
import site.goldenticket.common.constants.AreaCode;

@Builder
public record WishRegionResponse(
    Long wishRegionId,
    Long userId,
    AreaCode areaCode
) {

}
