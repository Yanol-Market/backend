package site.goldenticket.domain.wishRegion.dto;

import lombok.Builder;
import site.goldenticket.common.constants.AreaCode;

@Builder
public record WishRegionResponse(
    Long wishRegionId,
    Long userId,
    AreaCode areaCode
) {

}
