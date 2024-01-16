package site.goldenticket.domain.product.search.dto;

public record SearchRankingResponse(
        String keyword,
        int score
) {

    public static SearchRankingResponse fromTuple(String keyword, int score) {
        return new SearchRankingResponse(
                keyword,
                score
        );
    }
}
