package site.goldenticket.domain.product.dto;

import org.springframework.data.domain.Slice;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.model.Product;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record SearchProductResponse(
        String areaName,
        String keyword,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        String priceRange,
        long totalCount,
        List<ProductResponse> productResponseList
) {

    public static SearchProductResponse fromEntity(
            AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate, PriceRange priceRange, long totalCount, Slice<Product> productSlice
    ) {
        List<ProductResponse> productResponseList = productSlice.getContent().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());

        return new SearchProductResponse(
                areaCode.getAreaName(),
                keyword,
                checkInDate,
                checkOutDate,
                priceRange.getLabel(),
                totalCount,
                productResponseList
        );
    }
}
