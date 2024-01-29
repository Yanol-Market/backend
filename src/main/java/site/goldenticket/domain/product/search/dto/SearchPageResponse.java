package site.goldenticket.domain.product.search.dto;

import java.util.List;

public record SearchPageResponse(
        List<SearchHistoryResponse> searchHistoryList,
        List<SearchRankingResponse> searchKeywordRankingList,
        List<SearchRankingResponse> searchAreaRankingList
) {

    public static SearchPageResponse fromEntity(List<SearchHistoryResponse> searchHistoryResponseList, List<SearchRankingResponse> searchKeywordRankingList, List<SearchRankingResponse> searchAreaRankingList) {
        return new SearchPageResponse(
                searchHistoryResponseList,
                searchKeywordRankingList,
                searchAreaRankingList
        );
    }
}
