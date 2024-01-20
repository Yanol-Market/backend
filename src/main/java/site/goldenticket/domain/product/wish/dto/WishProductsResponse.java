package site.goldenticket.domain.product.wish.dto;

import lombok.Builder;
import site.goldenticket.domain.product.wish.entity.WishProduct;

import java.util.List;

@Builder
public record WishProductsResponse(
        List<WishProductResponse> wishProducts
) {

    public static WishProductsResponse of(List<WishProduct> wishProducts) {
        return WishProductsResponse.builder()
                .wishProducts(createWishProductResponse(wishProducts))
                .build();
    }

    private static List<WishProductResponse> createWishProductResponse(List<WishProduct> wishProducts) {
        return wishProducts.stream()
                .map(WishProductResponse::of)
                .toList();
    }
}
