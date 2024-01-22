package site.goldenticket.domain.product.dto;

import java.util.List;

public record HomeProductResponse(
        List<WishedProductResponse> goldenPriceTop5,
        List<WishedProductResponse> viewCountTop5,
        List<WishedProductResponse> recentRegisteredTop5,
        List<WishedProductResponse> dayUseTop5
) {
    public static HomeProductResponse from(List<WishedProductResponse> goldenPriceTop5, List<WishedProductResponse> viewCountTop5, List<WishedProductResponse> recentRegisteredTop5,  List<WishedProductResponse> dayUseTop5) {

        return new HomeProductResponse(
                goldenPriceTop5,
                viewCountTop5,
                recentRegisteredTop5,
                dayUseTop5
        );
    }
}
