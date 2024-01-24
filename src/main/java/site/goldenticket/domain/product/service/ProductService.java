package site.goldenticket.domain.product.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import site.goldenticket.common.api.RestTemplateService;
import site.goldenticket.common.constants.PaginationConstants;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.domain.alert.service.AlertService;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.dto.*;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.CustomSlice;
import site.goldenticket.domain.product.repository.ProductRepository;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.wish.service.WishRegionService;
import site.goldenticket.dummy.reservation.dto.ReservationDetailsResponse;
import site.goldenticket.dummy.reservation.dto.YanoljaProductResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static site.goldenticket.common.redis.constants.RedisConstants.*;
import static site.goldenticket.common.response.ErrorCode.*;
import static site.goldenticket.domain.product.constants.DummyUrlConstants.*;
import static site.goldenticket.dummy.reservation.constants.ReservationStatus.NOT_REGISTERED;
import static site.goldenticket.dummy.reservation.constants.ReservationStatus.REGISTERED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final RestTemplateService restTemplateService;

    private final RedisService redisService;
    private final ProductRepository productRepository;
    private final AlertService alertService;
    private final WishRegionService wishRegionService;

    // 1. 키워드 검색 및 지역 검색 메서드
    @Transactional(readOnly = true)
    public Slice<SearchProductResponse> getProductsBySearch(
        AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate,
        PriceRange priceRange,
        LocalDate cursorCheckInDate, Long cursorId, Pageable pageable,
        PrincipalDetails principalDetails
    ) {
        Long userId = (principalDetails != null) ? principalDetails.getUserId() : null;

        boolean isAuthenticated = (userId != null);

        CustomSlice<Product> productSlice = productRepository.getProductsBySearch(
            areaCode, keyword, checkInDate, checkOutDate, priceRange, cursorCheckInDate, cursorId,
            pageable, userId
        );

        SearchProductResponse searchProductResponse = SearchProductResponse.fromEntity(
            areaCode, keyword, checkInDate, checkOutDate, priceRange,
            productSlice.getTotalElements(), productSlice, isAuthenticated
        );

        return new SliceImpl<>(
            Collections.singletonList(searchProductResponse),
            pageable,
            productSlice.hasNext()
        );
    }

    @Transactional(readOnly = true)
    public Slice<RegionProductResponse> getProductsByAreaCode(
        AreaCode areaCode, LocalDate cursorCheckInDate, Long cursorId, Pageable pageable,
        PrincipalDetails principalDetails
    ) {
        Long userId = (principalDetails != null) ? principalDetails.getUserId() : null;

        boolean isAuthenticated = (userId != null);

        CustomSlice<Product> productSlice = productRepository.getProductsByAreaCode(
            areaCode, cursorCheckInDate, cursorId, pageable, userId
        );

        RegionProductResponse regionProductResponse = RegionProductResponse.fromEntity(
            productSlice.getTotalElements(), productSlice, isAuthenticated
        );

        return new SliceImpl<>(
            Collections.singletonList(regionProductResponse),
            pageable,
            productSlice.hasNext()
        );
    }

    // 2. 야놀자 예약 정보 조회 메서드
    @Transactional
    public List<ReservationResponse> getAllReservations(Long yaUserId) {
        String getUrl = buildReservationUrl(RESERVATIONS_ENDPOINT, yaUserId);

        List<YanoljaProductResponse> yanoljaProductResponses = restTemplateService.getList(
            getUrl,
            YanoljaProductResponse[].class
        );

        List<ReservationResponse> reservationResponses = new ArrayList<>();

        for (YanoljaProductResponse yanoljaProductResponse : yanoljaProductResponses) {
            Long reservationId = yanoljaProductResponse.reservationId();
            Optional<Product> product = productRepository.findByReservationId(reservationId);
            reservationResponses.add(getReservationResponse(product, yanoljaProductResponse));
        }

        return reservationResponses;
    }

    private static ReservationResponse getReservationResponse(Optional<Product> product,
        YanoljaProductResponse yanoljaProductResponse) {
        if (product.isPresent()) {
            return ReservationResponse.from(yanoljaProductResponse, REGISTERED);
        }

        return ReservationResponse.from(yanoljaProductResponse, NOT_REGISTERED);
    }

    // 3. 상품 생성, 조회, 수정, 삭제 관련 메서드
    @Transactional
    public ProductResponse createProduct(
        ProductRequest productRequest, Long reservationId, PrincipalDetails principalDetails
    ) {
        if (productRepository.existsByReservationId(reservationId)) {
            throw new CustomException(PRODUCT_ALREADY_EXISTS);
        }

        String getUrl = buildReservationUrl(RESERVATION_ENDPOINT, reservationId);

        ReservationDetailsResponse reservationDetailsResponse = restTemplateService.get(
            getUrl,
            ReservationDetailsResponse.class
        ).orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

        Product product = productRequest.toEntity(reservationDetailsResponse,
            principalDetails.getUserId());
        Product savedProduct = productRepository.save(product);

        redisService.addZScore(AUTOCOMPLETE_KEY, product.getAccommodationName(),
            INITIAL_RANKING_SCORE);

        //해당 지역 찜한 회원들에게 알림 전송
        AreaCode areaCode = product.getAreaCode();
        List<Long> userList = wishRegionService.findUserIdByRegion(areaCode);
        for (Long userId : userList) {
            alertService.createAlert(userId,
                "관심있던 '" + areaCode.getAreaName() + "'지역에 새로운 상품이 등록되었습니다! 사라지기 전에 확인해보세요!");
        }

        return ProductResponse.fromEntity(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(ProductRequest productRequest, Long productId) {
        Product product = getProduct(productId);
        product.update(productRequest.goldenPrice(), productRequest.content());
        Product updatedProduct = productRepository.save(product);

        return ProductResponse.fromEntity(updatedProduct);
    }

    @Transactional
    public Long deleteProduct(Long productId) {
        Product product = getProduct(productId);

        productRepository.delete(product);

        return productId;
    }

    // 4. 기타 유틸 메서드
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }

    Product getProductWithWishProducts(Long productId, Long userId) {
        return productRepository.findProductWithWishProductsByProductIdAndUserId(productId, userId);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    private String buildReservationUrl(String endpoint, Long pathVariable) {
        return UriComponentsBuilder
            .fromUriString(DISTRIBUTE_BASE_URL)
            .path(endpoint)
            .buildAndExpand(pathVariable)
            .encode(StandardCharsets.UTF_8)
            .toUriString();
    }

    public String generateOrRetrieveAnonymousKey(HttpServletRequest request,
        HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            String anonymousKey = Arrays.stream(cookies)
                .filter(cookie -> "AnonymousKey".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

            if (anonymousKey == null) {
                anonymousKey = UUID.randomUUID().toString();
                ResponseCookie cookie = ResponseCookie.from("AnonymousKey", anonymousKey)
                    .domain(".golden-ticket.site")
                    .httpOnly(false)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .build();

                response.addHeader("Set-Cookie", cookie.toString());
            }

            return anonymousKey;
        } else {
            String anonymousKey = UUID.randomUUID().toString();
            ResponseCookie cookie = ResponseCookie.from("AnonymousKey", anonymousKey)
                .domain(".golden-ticket.site")
                .httpOnly(false)
                .secure(true)
                .path("/")
                .sameSite("None")
                .build();

            response.addHeader("Set-Cookie", cookie.toString());

            return anonymousKey;
        }
    }

    public void updateProductViewCount(String userKey, String productKey) {
        String viewProductKey = userKey.concat(":").concat("viewProductList");

        List<String> viewProductList = redisService.getList(viewProductKey, String.class);

        if (!viewProductList.contains(productKey)) {
            redisService.leftPush(viewProductKey, productKey);

            Double currentViewCount = redisService.getZScore(VIEW_RANKING_KEY, productKey);
            Double updateViewCount =
                (currentViewCount != null) ? currentViewCount + 1 : SCORE_INCREMENT_AMOUNT;
            redisService.addZScore(VIEW_RANKING_KEY, productKey, updateViewCount);
        }
    }

    public void updateAutocompleteCount(String autocompleteKey, String accommodationName) {
        Double currentAutocompleteCount = redisService.getZScore(autocompleteKey,
            accommodationName);
        Double updateAutocompleteCount =
            (currentAutocompleteCount != null) ? SCORE_INCREMENT_AMOUNT + 1
                : SCORE_INCREMENT_AMOUNT;
        redisService.addZScore(autocompleteKey, accommodationName, updateAutocompleteCount);
    }

    public HomeProductResponse getHomeProduct(PrincipalDetails principalDetails) {
        Long userId = (principalDetails != null) ? principalDetails.getUserId() : null;

        boolean isAuthenticated = (userId != null);

        Pageable pageable = PageRequest.of(PaginationConstants.DEFAULT_PAGE,
            PaginationConstants.MIN_PAGE_SIZE);

        List<WishedProductResponse> goldenPriceTop5 = getProductResponseList(
            productRepository.findTop5ByGoldenPriceAsc(userId, pageable), isAuthenticated);
        List<WishedProductResponse> viewCountTop5 = getProductResponseList(
            productRepository.findTop5ByViewCountDesc(userId, pageable), isAuthenticated);
        List<WishedProductResponse> recentRegisteredTop5 = getProductResponseList(
            productRepository.findTop5ByIdDesc(userId, pageable), isAuthenticated);
        List<WishedProductResponse> dayUseTop5 = getProductResponseList(
            productRepository.findTop5DayUseProductsCheckInDateAsc(userId, pageable),
            isAuthenticated);

        return new HomeProductResponse(
            goldenPriceTop5,
            viewCountTop5,
            recentRegisteredTop5,
            dayUseTop5
        );
    }

    private List<WishedProductResponse> getProductResponseList(List<Product> productList,
        boolean isAuthenticated) {
        return productList.stream()
            .map(
                product -> WishedProductResponse.fromEntity(product, isAuthenticated))
            .collect(Collectors.toList());
    }

    public List<Product> findProductListByUserId(Long userId) {
        return productRepository.findAllByUserId(userId);
    }

    @Transactional
    public void updateProductForNego(Product product) {
        productRepository.save(product);
    }

    public List<Product> findByProductStatusInAndUserId(List<ProductStatus> productStatusList,
        Long userId) {
        return productRepository.findByProductStatusInAndUserId(productStatusList, userId);
    }

    public Product findByProductStatusAndProductId(ProductStatus productStatus, Long productId) {
        return productRepository.findByProductStatusAndId(productStatus, productId);
    }
}
