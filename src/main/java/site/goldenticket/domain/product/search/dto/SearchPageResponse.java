package site.goldenticket.domain.product.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchPageResponse {

    private List<SearchHistoryResponse> searchHistoryList;
    private List<SearchRankingResponse> searchKeywordRankingList;
    private List<SearchRankingResponse> searchAreaRankingList;

    public static SearchPageResponse fromEntity(List<SearchHistoryResponse> searchHistoryResponseList, List<SearchRankingResponse> searchKeywordRankingList, List<SearchRankingResponse> searchAreaRankingList) {

        return SearchPageResponse.builder()
                .searchHistoryList(searchHistoryResponseList)
                .searchKeywordRankingList(searchKeywordRankingList)
                .searchAreaRankingList(searchAreaRankingList)
                .build();
    }
}
