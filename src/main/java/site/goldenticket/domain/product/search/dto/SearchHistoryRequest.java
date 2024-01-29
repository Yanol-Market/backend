package site.goldenticket.domain.product.search.dto;

import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.search.model.SearchHistory;

import java.time.LocalDate;

public record SearchHistoryRequest(
        AreaCode areaCode,
        String keyword,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        PriceRange priceRange
) {

    public static SearchHistory toEntity(AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate, PriceRange priceRange) {
        return SearchHistory.builder()
                .areaName(areaCode.getAreaName())
                .keyword(keyword)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .priceRange(priceRange.getLabel())
                .build();
    }
}
