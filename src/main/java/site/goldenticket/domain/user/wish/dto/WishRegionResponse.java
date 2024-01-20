package site.goldenticket.domain.user.wish.dto;

import lombok.Builder;
import site.goldenticket.domain.product.constants.AreaCode;

@Builder
public record WishRegionResponse(
    Long wishRegionId,
    Long userId,
    AreaCode areaCode
) {

}
