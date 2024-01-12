package site.goldenticket.domain.search.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.search.model.SearchHistory;

import java.time.LocalDate;

@Getter
@Builder
public class SearchHistoryResponse {

    private Long id;
    private String areaName;
    private String keyword;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String priceRange;

    public static SearchHistoryResponse fromEntity(SearchHistory searchHistory) {

        return SearchHistoryResponse.builder()
                .id(searchHistory.getId())
                .areaName(searchHistory.getAreaName())
                .keyword(searchHistory.getKeyword())
                .checkInDate(searchHistory.getCheckInDate())
                .checkOutDate(searchHistory.getCheckOutDate())
                .priceRange(searchHistory.getPriceRange())
                .build();
    }
}
