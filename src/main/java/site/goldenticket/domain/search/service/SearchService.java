package site.goldenticket.domain.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.redis.constants.RedisConstants;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.domain.search.dto.SearchHistoryRequest;
import site.goldenticket.domain.search.dto.SearchResponse;
import site.goldenticket.domain.search.model.SearchHistory;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final RedisService redisService;

    @Transactional
    public String createRecentSearchHistory(SearchHistoryRequest searchHistoryRequest) {
        SearchHistory searchHistory = SearchHistoryRequest.toEntity(searchHistoryRequest);

        // TODO : 사용자 별 이메일로 키 값 설정 하기
        // TODO : 로그인 한 사용자에 한해서 검색 기록 저장
        String userKey = "test@email.com";
        String searchHistoryKey = userKey.concat(":").concat("searchHistory");
        List<SearchHistory> searchHistoryList = redisService.getList(searchHistoryKey, SearchHistory.class);

        boolean isDuplicate = searchHistoryList.stream().anyMatch(searchHistory::equals);

        if (!isDuplicate) {
            long size = redisService.opsForListSize(searchHistoryKey);

            if (size == RedisConstants.MAX_SEARCH_HISTORY_SIZE) {
                Object poppedValue = redisService.rightPop(searchHistoryKey);
                log.info("가장 오래된 검색 기록 삭제: {}", poppedValue);
            }

            redisService.leftPush(searchHistoryKey, searchHistory);
            log.info("새로운 검색 기록 추가: {}", searchHistory);
        }

        createSearchRanking(searchHistoryRequest.getKeyword());

        return searchHistoryRequest.getKeyword();
    }

    @Transactional
    public void createSearchRanking(String keyword) {
        String rankingKey = "searchRanking";
        Double currentScore = redisService.getZScore(rankingKey, keyword);

        Double newScore = (currentScore != null) ? currentScore + RedisConstants.INCREMENT_AMOUNT : RedisConstants.INCREMENT_AMOUNT;

        redisService.addZScore(rankingKey, keyword, newScore);
        log.info("검색어 저장 완료: '{}'", keyword);
    }

    @Transactional(readOnly = true)
    public SearchResponse getUserSearchAndRankingInfo() {
        String userKey = "test@email.com";
        if (userKey == null) {
            return SearchResponse.fromEntity(Collections.emptyList(), getSearchRanking());
        }

        return SearchResponse.fromEntity(getSearchHistory(userKey), getSearchRanking());
    }

    public List<SearchHistory> getSearchHistory(String userKey) {
        String searchHistoryKey = userKey.concat(":").concat("searchHistory");
        return redisService.getList(searchHistoryKey, SearchHistory.class);
    }

    public List<String> getSearchRanking() {
        String rankingKey = "searchRanking";
        Set<ZSetOperations.TypedTuple<String>> searchRanking = redisService.getZRanking(rankingKey, 0, RedisConstants.TOP_RANKING_COUNT - 1);

        return searchRanking.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .collect(Collectors.toList());
    }
}
