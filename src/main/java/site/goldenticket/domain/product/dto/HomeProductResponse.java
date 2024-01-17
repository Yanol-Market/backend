package site.goldenticket.domain.product.dto;

import java.util.List;

public record HomeProductResponse(
        List<ProductResponse> goldenPriceTop5,
        List<ProductResponse> viewCountTop5,
        List<ProductResponse> recentRegisteredTop5,
        List<ProductResponse> dayUseTop5
) {
    public static HomeProductResponse from(List<ProductResponse> goldenPriceTop5, List<ProductResponse> viewCountTop5, List<ProductResponse> recentRegisteredTop5,  List<ProductResponse> dayUseTop5) {

        return new HomeProductResponse(
                goldenPriceTop5,
                viewCountTop5,
                recentRegisteredTop5,
                dayUseTop5
        );
    }
}
