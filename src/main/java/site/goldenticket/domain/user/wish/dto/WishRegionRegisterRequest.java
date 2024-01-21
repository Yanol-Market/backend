package site.goldenticket.domain.user.wish.dto;

import jakarta.validation.constraints.NotEmpty;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.user.wish.entity.WishRegion;

import java.util.List;

public record WishRegionRegisterRequest(
    @NotEmpty(message = "지역코드를 필수로 입력하셔야 합니다.")
    List<AreaCode> regions
) {

    public List<WishRegion> toEntity() {
        return regions.stream()
                .map(this::getWishRegion)
                .toList();
    }

    private WishRegion getWishRegion(AreaCode region) {
        return WishRegion.builder()
                .areaCode(region)
                .build();
    }
}
