package site.goldenticket.domain.product.search.dto;

import lombok.Builder;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.search.model.SearchHistory;

import java.time.LocalDate;

@Builder
public class SearchHistoryRequest {

    private AreaCode areaCode;
    private String keyword;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private PriceRange priceRange;

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
