package site.goldenticket.domain.search.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.common.constants.AreaCode;
import site.goldenticket.common.constants.PriceRange;
import site.goldenticket.domain.search.model.SearchHistory;

import java.time.LocalDate;

@Getter
@Builder
public class SearchHistoryRequest {

    private AreaCode areaCode;
    private String keyword;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private PriceRange priceRange;

    public SearchHistory toEntity() {
        return SearchHistory.builder()
                .areaName(areaCode.getAreaName())
                .keyword(keyword)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .priceRange(priceRange.getLabel())
                .build();
    }
}
