package site.goldenticket.domain.search.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.search.model.SearchHistory;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SearchResponse {

    private List<SearchHistoryResponse> searchHistoryList;
    private List<String> searchRankingList;

    public static SearchResponse fromEntity(List<SearchHistory> searchHistoryList, List<String> searchRankingList) {

        List<SearchHistoryResponse> searchHistoryResponseList = searchHistoryList.stream()
                .map(SearchHistoryResponse::fromEntity)
                .collect(Collectors.toList());

        return SearchResponse.builder()
                .searchHistoryList(searchHistoryResponseList)
                .searchRankingList(searchRankingList)
                .build();
    }
}

