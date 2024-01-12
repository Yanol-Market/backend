package site.goldenticket.domain.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeProductResponse {
    List<ProductResponse> goldenPriceTop5;
    List<ProductResponse> viewCountTop5;
    List<ProductResponse> recentRegisteredTop5;
    List<ProductResponse> dayUseTop5;
}
