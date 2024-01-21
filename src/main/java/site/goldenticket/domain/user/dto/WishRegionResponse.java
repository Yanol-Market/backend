package site.goldenticket.domain.user.dto;

import lombok.Builder;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.user.wish.entity.WishRegion;

@Builder
public record WishRegionResponse(
        Long id,
        AreaCode region
) {

    public static WishRegionResponse from(WishRegion wishRegion) {
        return WishRegionResponse.builder()
                .id(wishRegion.getId())
                .region(wishRegion.getRegion())
                .build();
    }
}
