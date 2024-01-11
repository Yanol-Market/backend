package site.goldenticket.domain.search.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.search.model.SearchHistory;

import java.time.LocalDate;

@Getter
@Builder
public class SearchHistoryResponse {

    String keyword;
    String area;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    String priceRange;

    public static SearchHistoryResponse fromEntity(SearchHistory searchHistory) {

        return SearchHistoryResponse.builder()
                .keyword(searchHistory.getKeyword())
                .area(searchHistory.getArea())
                .checkInDate(searchHistory.getCheckInDate())
                .checkOutDate(searchHistory.getCheckOutDate())
                .priceRange(searchHistory.getPriceRange())
                .build();
    }
}
