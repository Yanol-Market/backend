package site.goldenticket.domain.user.wish.dto;

import lombok.Builder;
import site.goldenticket.domain.user.wish.entity.WishRegion;

import java.util.List;

@Builder
public record WishRegionsResponse(
        List<WishRegionResponse> wishRegions
) {

    public static WishRegionsResponse from(List<WishRegion> wishRegions) {
        return WishRegionsResponse.builder()
                .wishRegions(createWishRegionResponse(wishRegions))
                .build();
    }

    private static List<WishRegionResponse> createWishRegionResponse(List<WishRegion> wishRegions) {
        return wishRegions.stream()
                .map(WishRegionResponse::from)
                .toList();
    }
}
