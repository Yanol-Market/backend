package site.goldenticket.domain.product.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.product.search.dto.SearchPageResponse;
import site.goldenticket.domain.product.search.service.SearchService;
import site.goldenticket.domain.security.PrincipalDetails;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<SearchPageResponse>> getSearchPage(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(CommonResponse.ok("검색 페이지 정보가 성공적으로 조회되었습니다.", searchService.getSearchPage(principalDetails)));
    }

    @GetMapping("/search/autocomplete")
    public ResponseEntity<CommonResponse<List<String>>> getAutocompleteKeywords(
            @RequestParam String prefix
    ) {
        return ResponseEntity.ok(CommonResponse.ok("자동완성 결과를 성공적으로 마쳤습니다.", searchService.getAutocompleteKeywords(prefix)));
    }

    @DeleteMapping("/search/history/{searchHistoryId}")
    public ResponseEntity<CommonResponse<Long>> deleteUserSearchHistory(
            @PathVariable Long searchHistoryId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(CommonResponse.ok("사용자 검색 기록이 성공적으로 삭제되었습니다.", searchService.deleteSearchHistory(searchHistoryId, principalDetails)));
    }
}
