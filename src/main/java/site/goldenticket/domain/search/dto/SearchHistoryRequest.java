package site.goldenticket.domain.search.dto;

import lombok.Getter;
import site.goldenticket.domain.search.model.SearchHistory;

import java.time.LocalDate;

@Getter
public class SearchHistoryRequest {

    String keyword;
    String area;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    String priceRange;

    public static SearchHistory toEntity(SearchHistoryRequest searchHistoryRequest) {

        return SearchHistory.builder()
                .keyword(searchHistoryRequest.keyword)
                .area(searchHistoryRequest.area)
                .checkInDate(searchHistoryRequest.checkInDate)
                .checkOutDate(searchHistoryRequest.checkOutDate)
                .priceRange(searchHistoryRequest.priceRange)
                .build();
    }
}
