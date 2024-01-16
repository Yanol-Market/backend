package site.goldenticket.domain.product.search.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.product.search.model.SearchHistory;

import java.time.LocalDate;

@Getter
@Builder
public class SearchHistoryResponse {

    private Long searchHistoryId;
    private String areaName;
    private String keyword;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String priceRange;

    public static SearchHistoryResponse fromEntity(SearchHistory searchHistory) {

        return SearchHistoryResponse.builder()
                .searchHistoryId(searchHistory.getId())
                .areaName(searchHistory.getAreaName())
                .keyword(searchHistory.getKeyword())
                .checkInDate(searchHistory.getCheckInDate())
                .checkOutDate(searchHistory.getCheckOutDate())
                .priceRange(searchHistory.getPriceRange())
                .build();
    }
}
