package site.goldenticket.domain.user.wish.dto;

import lombok.Builder;
import site.goldenticket.domain.user.wish.entity.WishProduct;

@Builder
public record WishProductSaveResponse(Long id) {

    public static WishProductSaveResponse of(WishProduct wishProduct) {
        return WishProductSaveResponse.builder()
                .id(wishProduct.getId())
                .build();
    }
}
