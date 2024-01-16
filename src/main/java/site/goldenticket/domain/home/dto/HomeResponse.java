package site.goldenticket.domain.home.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.product.dto.HomeProductResponse;

@Getter
@Builder
public class HomeResponse {
    private HomeProductResponse homeProductResponse;
}
