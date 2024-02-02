package site.goldenticket.domain.product.dto;

import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.CustomSlice;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record SearchProductResponse(
        String areaName,
        String keyword,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        String priceRange,
        List<WishedProductResponse> wishedProductResponseList
) {

    public static SearchProductResponse fromEntity(
            AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate,
            PriceRange priceRange, CustomSlice<Product> productSlice, boolean isAuthenticated) {

        List<WishedProductResponse> wishedProductResponseList = productSlice.getContent().stream()
                .map(
                        product -> WishedProductResponse.fromEntity(product, isAuthenticated)
                )
                .collect(Collectors.toList());

        return new SearchProductResponse(
                areaCode.getAreaName(),
                keyword,
                checkInDate,
                checkOutDate,
                priceRange.getLabel(),
                wishedProductResponseList
        );
    }
}
