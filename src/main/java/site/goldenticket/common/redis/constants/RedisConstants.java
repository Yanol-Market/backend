package site.goldenticket.common.redis.constants;

public class RedisConstants {
    // Search
    public static final int MAX_AUTOCOMPLETE_SIZE = 5;

    // History
    public static final long MAX_SEARCH_HISTORY_SIZE = 5;

    // Rank
    public static final double INITIAL_RANKING_SCORE = 0.0;
    public static final double SCORE_INCREMENT_AMOUNT = 1.0;
    public static final String KEYWORD_RANKING_KEY = "keywordRanking";
    public static final String AREA_RANKING_KEY = "areaRanking";
    public static final String VIEW_RANKING_KEY = "viewRanking";
    public static final String AUTOCOMPLETE_KEY = "autocomplete";
    public static final int MAX_KEYWORD_RANKING_SIZE = 10;
    public static final int MAX_AREA_RANKING_SIZE = 5;
}

