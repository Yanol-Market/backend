package site.goldenticket.domain.product.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.redis.constants.RedisConstants;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.search.dto.SearchHistoryRequest;
import site.goldenticket.domain.product.search.dto.SearchHistoryResponse;
import site.goldenticket.domain.product.search.dto.SearchPageResponse;
import site.goldenticket.domain.product.search.dto.SearchRankingResponse;
import site.goldenticket.domain.product.search.model.SearchHistory;
import site.goldenticket.domain.product.util.IdGeneratorUtil;
import site.goldenticket.domain.security.PrincipalDetails;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static site.goldenticket.common.redis.constants.RedisConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final RedisService redisService;

    @Transactional(readOnly = true)
    public List<String> getAutocompleteKeywords(String searchPrefix) {
        Set<String> autocompleteSet = redisService.getZRangeByLex(AUTOCOMPLETE_KEY, searchPrefix, MAX_AUTOCOMPLETE_SIZE);

        return new ArrayList<>(autocompleteSet);
    }

    @Transactional(readOnly = true)
    public SearchPageResponse getSearchPage(PrincipalDetails principalDetails) {
        List<SearchHistoryResponse> searchHistoryList = getSearchHistory(principalDetails);
        List<SearchRankingResponse> keywordRankingList = getSearchRanking(KEYWORD_RANKING_KEY, MAX_KEYWORD_RANKING_SIZE);
        List<SearchRankingResponse> areaRankingList = getSearchRanking(AREA_RANKING_KEY, MAX_AREA_RANKING_SIZE);

        return SearchPageResponse.fromEntity(searchHistoryList, keywordRankingList, areaRankingList);
    }

    public List<SearchRankingResponse> getSearchRanking(String rankingKey, long maxSize) {
        Set<ZSetOperations.TypedTuple<String>> rankSet = redisService.getZRank(rankingKey, 0, maxSize - 1);

        List<SearchRankingResponse> searchRankingResponseList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> rankTuple : rankSet) {
            searchRankingResponseList.add(SearchRankingResponse.fromTuple(rankTuple.getValue(), rankTuple.getScore().intValue()));
        }

        return searchRankingResponseList;
    }

    @Transactional
    public void updateSearchRanking(String rankingKey, String searchItem) {
        Double currentScore = redisService.getZScore(rankingKey, searchItem);
        Double newScore = (currentScore != null) ? currentScore + SCORE_INCREMENT_AMOUNT : SCORE_INCREMENT_AMOUNT;

        redisService.addZScore(rankingKey, searchItem, newScore);

        log.info("실시간 검색 집계 완료. search item: '{}', current score: '{}'", searchItem, newScore);
    }

    public List<SearchHistoryResponse> getSearchHistory(PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            log.warn("인증되지 않은 사용자입니다. 최근 검색 기록을 조회할 수 없습니다.");
            return Collections.emptyList();
        }

        String userKey = principalDetails.getUsername();
        String searchHistoryKey = userKey.concat(":").concat("searchHistory");

        List<SearchHistory> searchHistoryList = redisService.getList(searchHistoryKey, SearchHistory.class);

        return searchHistoryList.stream()
                .map(SearchHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createSearchHistory(AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate, PriceRange priceRange, PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            log.warn("인증 되지 않은 사용자 입니다. 최근 검색 기록을 생성할 수 없습니다.");
            return;
        }

        SearchHistory searchHistory = SearchHistoryRequest.toEntity(areaCode, keyword, checkInDate, checkOutDate, priceRange);

        String userKey = principalDetails.getUsername();
        String searchHistoryKey = userKey.concat(":").concat("searchHistory");
        List<SearchHistory> searchHistoryList = redisService.getList(searchHistoryKey, SearchHistory.class);

        boolean isDuplicate = searchHistoryList.stream().anyMatch(searchHistory::equals);

        if (!isDuplicate) {
            if (searchHistoryList.size() == RedisConstants.MAX_SEARCH_HISTORY_SIZE) {
                redisService.rightPop(searchHistoryKey);
                log.info("가장 오래된 검색 기록 삭제. user: {}", userKey);
            }

            searchHistory.setId(IdGeneratorUtil.createID());

            redisService.leftPush(searchHistoryKey, searchHistory);
            log.info("새로운 검색 기록 추가. user: {}, new search history: {}", userKey, searchHistory);
        }
    }

    @Transactional
    public Long deleteSearchHistory(Long searchHistoryId, PrincipalDetails principalDetails) {
        String userKey = principalDetails.getUsername();
        String searchHistoryKey = userKey.concat(":").concat("searchHistory");
        List<SearchHistory> searchHistoryList = redisService.getList(searchHistoryKey, SearchHistory.class);

        Optional<SearchHistory> searchHistory = searchHistoryList.stream()
                .filter(history -> history.getId().equals(searchHistoryId))
                .findFirst();

        if (searchHistory.isPresent()) {
            redisService.removeList(searchHistoryKey, searchHistory.get());

            log.info("검색어 삭제 완료. search history id: '{}'", searchHistoryId);
        } else {
            throw new CustomException(ErrorCode.SEARCH_HISTORY_NOT_FOUND);
        }

        return searchHistoryId;
    }
}
