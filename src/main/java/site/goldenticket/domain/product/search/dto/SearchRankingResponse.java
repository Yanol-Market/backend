package site.goldenticket.domain.product.search.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchRankingResponse {

    private String keyword;
    private int score;

    public static SearchRankingResponse fromTuple(String keyword, int score) {
        return SearchRankingResponse.builder()
                .keyword(keyword)
                .score(score)
                .build();
    }
}
