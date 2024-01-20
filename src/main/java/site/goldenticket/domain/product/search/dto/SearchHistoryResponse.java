package site.goldenticket.domain.product.search.dto;

import site.goldenticket.domain.product.search.model.SearchHistory;

import java.time.LocalDate;

public record SearchHistoryResponse(
        Long searchHistoryId,
        String areaName,
        String keyword,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        String priceRange
) {

    public static SearchHistoryResponse fromEntity(SearchHistory searchHistory) {

        return new SearchHistoryResponse(
                searchHistory.getId(),
                searchHistory.getAreaName(),
                searchHistory.getKeyword(),
                searchHistory.getCheckInDate(),
                searchHistory.getCheckOutDate(),
                searchHistory.getPriceRange()
        );
    }
}
