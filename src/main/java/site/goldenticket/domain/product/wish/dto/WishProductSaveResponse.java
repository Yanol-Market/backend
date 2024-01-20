package site.goldenticket.domain.product.wish.dto;

import lombok.Builder;
import site.goldenticket.domain.product.wish.entity.WishProduct;

@Builder
public record WishProductSaveResponse(Long id) {

    public static WishProductSaveResponse of(WishProduct wishProduct) {
        return WishProductSaveResponse.builder()
                .id(wishProduct.getId())
                .build();
    }
}
