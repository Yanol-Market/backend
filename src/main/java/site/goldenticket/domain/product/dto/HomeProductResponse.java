package site.goldenticket.domain.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeProductResponse {
    private List<ProductResponse> goldenPriceTop5;
    private List<ProductResponse> viewCountTop5;
    private List<ProductResponse> recentRegisteredTop5;
    private List<ProductResponse> dayUseTop5;
}
