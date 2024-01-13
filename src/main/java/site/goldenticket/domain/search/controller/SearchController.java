package site.goldenticket.domain.search.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.search.dto.SearchHistoryRequest;
import site.goldenticket.domain.search.dto.SearchHistoryResponse;
import site.goldenticket.domain.search.dto.SearchResponse;
import site.goldenticket.domain.search.service.SearchService;


@RestController
@RequestMapping("/searches")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<CommonResponse<SearchHistoryResponse>> createRecentSearchHistory(@Valid @RequestBody SearchHistoryRequest searchHistoryRequest) {
        return ResponseEntity.ok(CommonResponse.ok("사용자 검색 기록 및 실시간 검색어 저장 성공", searchService.createRecentSearchHistory(searchHistoryRequest)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<SearchResponse>> getRecentSearchHistory() {
        return ResponseEntity.ok(CommonResponse.ok("사용자 검색어 및 실시간 검색어 조회 성공", searchService.getRecentSearchHistory()));
    }

    @DeleteMapping("/{searchHistoryId}")
    public ResponseEntity<CommonResponse<Long>> deleteUserSearchHistory(@PathVariable Long searchHistoryId) {
        return ResponseEntity.ok(CommonResponse.ok("사용자 검색어 삭제 성공", searchService.deleteUserSearchHistory(searchHistoryId)));
    }
}
