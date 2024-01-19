package site.goldenticket.domain.product.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.constants.PaginationConstants;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.dto.*;
import site.goldenticket.domain.product.search.service.SearchService;
import site.goldenticket.domain.product.service.ProductOrderService;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.dummy.reservation.dto.ReservationResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static site.goldenticket.common.redis.constants.RedisConstants.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SearchService searchService;
    private final ProductOrderService productOrderService;

    @GetMapping
    public CompletableFuture<ResponseEntity<CommonResponse<Slice<SearchProductResponse>>>> getProductsBySearch(
            @RequestParam AreaCode areaCode,
            @RequestParam String keyword,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate,
            @RequestParam PriceRange priceRange,
            @RequestParam(required = false) LocalDate cursorCheckInDate,
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(
                    page = PaginationConstants.DEFAULT_PAGE,
                    size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_FIELD,
                    direction = Sort.Direction.ASC
            ) Pageable pageable,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        CompletableFuture<Slice<SearchProductResponse>> searchProductFuture = CompletableFuture.supplyAsync(() ->
                productService.getProductsBySearch(areaCode, keyword, checkInDate, checkOutDate, priceRange, cursorCheckInDate, cursorId, pageable)
        );

        CompletableFuture<Void> searchHistoryFuture = CompletableFuture.runAsync(() ->
                searchService.createSearchHistory(areaCode, keyword, checkInDate, checkOutDate, priceRange, principalDetails)
        );

        CompletableFuture<Void> updateSearchKeywordRankingFuture = CompletableFuture.runAsync(() ->
                searchService.updateSearchRanking(KEYWORD_RANKING_KEY, keyword)
        );

        return CompletableFuture.allOf(searchProductFuture, searchHistoryFuture, updateSearchKeywordRankingFuture)
                .thenApply(ignoredVoid -> ResponseEntity.ok(CommonResponse.ok("키워드 검색 결과 조회 및 저장 성공.", searchProductFuture.join())));
    }

    @GetMapping("/region")
    public CompletableFuture<ResponseEntity<CommonResponse<Slice<RegionProductResponse>>>> getProductsByAreaCode(
            @RequestParam AreaCode areaCode,
            @RequestParam(required = false) LocalDate cursorCheckInDate,
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(
                    page = PaginationConstants.DEFAULT_PAGE,
                    size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_FIELD,
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        CompletableFuture<Slice<RegionProductResponse>> regionProductFuture = CompletableFuture.supplyAsync(() ->
                productService.getProductsByAreaCode(areaCode, cursorCheckInDate, cursorId, pageable)
        );

        CompletableFuture<Void> updateSearchAreaRankingFuture = CompletableFuture.runAsync(() ->
                searchService.updateSearchRanking(AREA_RANKING_KEY, areaCode.getAreaName())
        );

        return CompletableFuture.allOf(regionProductFuture, updateSearchAreaRankingFuture)
                .thenApply(ignoredVoid -> ResponseEntity.ok(CommonResponse.ok("지역 검색 결과 조회 및 저장 성공", regionProductFuture.join())));
    }

    @GetMapping("/reservations/{yaUserId}")
    public ResponseEntity<CommonResponse<List<ReservationResponse>>> getAllReservations(
            @PathVariable Long yaUserId
    ) {
        return ResponseEntity.ok(CommonResponse.ok("예약 정보 조회가 완료되었습니다.",  productService.getAllReservations(yaUserId)));
    }

    @PostMapping("/{reservationId}")
    public ResponseEntity<CommonResponse<ProductResponse>> createProduct(
            @PathVariable Long reservationId,
            @RequestBody @Validated ProductRequest productRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(CommonResponse.ok("상품이 성공적으로 등록이 완료되었습니다.", productService.createProduct(productRequest, reservationId, principalDetails)));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductDetailResponse>> getProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(CommonResponse.ok("상품이 성공적으로 조회가 완료되었습니다.", productService.getProduct(productId, principalDetails, request, response)));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @RequestBody @Validated ProductRequest productRequest
    ) {
        return ResponseEntity.ok(CommonResponse.ok("상품이 성공적으로 수정이 완료되었습니다.", productService.updateProduct(productRequest, productId)));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CommonResponse<Long>> deleteProduct(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(CommonResponse.ok("상품이 성공적으로 삭제가 완료되었습니다.", productService.deleteProduct(productId)));
    }

    @GetMapping("/history/progress")
    public ResponseEntity<CommonResponse<List<ProductProgressHistoryResponse>>> getProgressProducts(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(CommonResponse.ok("판매중인 상품이 성공적으로 조회가 완료되었습니다.", productOrderService.getProgressProducts(principalDetails.getUserId())));
    }

    @GetMapping("/history/completed")
    public ResponseEntity<CommonResponse<List<ProductCompletedHistoryResponse>>> getAllCompletedProducts(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(CommonResponse.ok("판매완료 된 상품이 성공적으로 조회가 완료되었습니다.", productOrderService.getAllCompletedProducts(principalDetails.getUserId())));
    }

    @GetMapping("/history/completed/{productId}")
    public ResponseEntity<CommonResponse<?>> getCompletedProductDetails(
            @RequestParam ProductStatus productStatus,
            @PathVariable Long productId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (productStatus == ProductStatus.SOLD_OUT) {
            return ResponseEntity.ok(CommonResponse.ok(productOrderService.getSoldOutCaseProductDetails(productId)));
        } else if(productStatus == ProductStatus.EXPIRED) {
            return ResponseEntity.ok(CommonResponse.ok(productOrderService.getExpiredCaseProductDetails(productId)));
        } else {
            throw new CustomException(ErrorCode.COMMON_SYSTEM_ERROR);
        }
    }
}
