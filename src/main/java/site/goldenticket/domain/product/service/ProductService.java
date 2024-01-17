package site.goldenticket.domain.product.service;

import static site.goldenticket.common.redis.constants.RedisConstants.INITIAL_RANKING_SCORE;
import static site.goldenticket.common.redis.constants.RedisConstants.SCORE_INCREMENT_AMOUNT;
import static site.goldenticket.common.redis.constants.RedisConstants.VIEW_RANKING_KEY;
import static site.goldenticket.common.response.ErrorCode.PRODUCT_ALREADY_EXISTS;
import static site.goldenticket.common.response.ErrorCode.PRODUCT_NOT_FOUND;
import static site.goldenticket.domain.product.constants.ApiEndpoints.DISTRIBUTE_BAE_URL;
import static site.goldenticket.domain.product.constants.ApiEndpoints.RESERVATIONS_ENDPOINT;
import static site.goldenticket.domain.product.constants.ApiEndpoints.RESERVATION_ENDPOINT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import site.goldenticket.common.constants.PaginationConstants;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.constants.UserType;
import site.goldenticket.domain.product.dto.HomeProductResponse;
import site.goldenticket.domain.product.dto.ProductDetailResponse;
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.dto.ProductResponse;
import site.goldenticket.domain.product.dto.RegionProductResponse;
import site.goldenticket.domain.product.dto.ReservationDetailsResponse;
import site.goldenticket.domain.product.dto.ReservationResponse;
import site.goldenticket.domain.product.dto.SearchProductResponse;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.CustomSlice;
import site.goldenticket.domain.product.repository.ProductRepository;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.dummy.reservation.constants.ReservationStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private final RedisService redisService;
    private final ProductRepository productRepository;

    // 1. 상품 검색 관련 메서드
    @Transactional(readOnly = true)
    public Slice<SearchProductResponse> getProductsBySearch(
        AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate,
        PriceRange priceRange,
        LocalDate cursorCheckInDate, Long cursorId, Pageable pageable
    ) {
        CustomSlice<Product> productSlice = productRepository.getProductsBySearch(
            areaCode, keyword, checkInDate, checkOutDate, priceRange, cursorCheckInDate, cursorId,
            pageable
        );

        SearchProductResponse searchProductResponse = SearchProductResponse.fromEntity(
            areaCode, keyword, checkInDate, checkOutDate, priceRange,
            productSlice.getTotalElements(), productSlice
        );

        return new SliceImpl<>(
            Collections.singletonList(searchProductResponse),
            pageable,
            productSlice.hasNext()
        );
    }

    @Transactional(readOnly = true)
    public Slice<RegionProductResponse> getProductsByAreaCode(
        AreaCode areaCode, LocalDate cursorCheckInDate, Long cursorId, Pageable pageable
    ) {
        CustomSlice<Product> productSlice = productRepository.getProductsByAreaCode(
            areaCode, cursorCheckInDate, cursorId, pageable
        );

        RegionProductResponse regionProductResponse = RegionProductResponse.fromEntity(
            productSlice.getTotalElements(), productSlice
        );

        return new SliceImpl<>(
            Collections.singletonList(regionProductResponse),
            pageable,
            productSlice.hasNext()
        );
    }

    // 2. 예약 상품 조회 관련 메서드
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations(Long yaUserId) {
        URI targetUrl = UriComponentsBuilder
            .fromUriString(DISTRIBUTE_BAE_URL)
            .path(RESERVATIONS_ENDPOINT)
            .buildAndExpand(yaUserId)
            .encode(StandardCharsets.UTF_8)
            .toUri();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(targetUrl, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();

            try {
                JsonNode dataNode = objectMapper.readTree(responseBody).get("data");

                List<ReservationResponse> reservationProductResponseList = new ArrayList<>();

                if (dataNode.isArray() && !dataNode.isEmpty()) {
                    for (JsonNode reservationNode : dataNode) {
                        ReservationResponse reservationProductResponse = objectMapper.treeToValue(
                            reservationNode, ReservationResponse.class);

                        ReservationStatus reservationStatus =
                            productRepository.existsByReservationId(
                                reservationProductResponse.getReservationId())
                                ? ReservationStatus.REGISTERED : ReservationStatus.NOT_REGISTERED;

                        reservationProductResponse = reservationProductResponse.toBuilder()
                            .reservationStatus(reservationStatus)
                            .build();

                        reservationProductResponseList.add(reservationProductResponse);
                    }
                } else {
                    return Collections.emptyList();
                }

                return reservationProductResponseList;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new CustomException(ErrorCode.COMMON_JSON_PROCESSING_ERROR);
            }
        } else {
            throw new CustomException(ErrorCode.RESERVATION_INTERNAL_SERVER_ERROR);
        }
    }

    // 3. 상품 생성, 조회, 수정, 삭제 관련 메서드
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest, Long reservationId,
        PrincipalDetails principalDetails) {
        if (productRepository.existsByReservationId(reservationId)) {
            throw new CustomException(PRODUCT_ALREADY_EXISTS);
        }

        Long userId = principalDetails.getUserId();

        URI targetUrl = UriComponentsBuilder
            .fromUriString(DISTRIBUTE_BAE_URL)
            .path(RESERVATION_ENDPOINT)
            .buildAndExpand(reservationId)
            .encode(StandardCharsets.UTF_8)
            .toUri();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(targetUrl, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();

            try {
                JsonNode reservationNode = objectMapper.readTree(responseBody).get("data");

                ReservationDetailsResponse reservationDetailsResponse = objectMapper.treeToValue(
                    reservationNode, ReservationDetailsResponse.class);
                Product product = productRepository.save(
                    productRequest.toEntity(reservationDetailsResponse, userId));

                redisService.addZScore(VIEW_RANKING_KEY, product.getAccommodationName(),
                    INITIAL_RANKING_SCORE);

                return ProductResponse.fromEntity(product);
            } catch (JsonProcessingException e) {
                throw new CustomException(ErrorCode.COMMON_JSON_PROCESSING_ERROR);
            }
        } else {
            throw new CustomException(ErrorCode.RESERVATION_INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(Long productId, PrincipalDetails principalDetails,
        HttpServletRequest request, HttpServletResponse response) {
        Product product = getProduct(productId);

        String userKey = (principalDetails != null) ? principalDetails.getUsername()
            : getOrCreateAnonymousKey(request, response);
        UserType userType =
            (principalDetails != null && principalDetails.getUserId().equals(product.getUserId()))
                ? UserType.SELLER : UserType.BUYER;

        updateProductViewCount(userKey, productId.toString());

        return ProductDetailResponse.fromEntity(product, userType);
    }

    @Transactional
    public ProductResponse updateProduct(ProductRequest productRequest, Long productId) {
        Product product = getProduct(productId);
        product.update(productRequest.getGoldenPrice(), productRequest.getContent());
        Product updatedProduct = productRepository.save(product);

        return ProductResponse.fromEntity(updatedProduct);
    }

    @Transactional
    public ProductResponse deleteProduct(Long productId) {
        Product product = getProduct(productId);
        productRepository.delete(product);

        return ProductResponse.fromEntity(product);
    }

    // 4. 기타 유틸 메서드
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }

    private String getOrCreateAnonymousKey(HttpServletRequest request,
        HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String anonymousKey = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("AnonymousKey".equals(cookie.getName())) {
                    anonymousKey = cookie.getValue();
                    break;
                }
            }
        }

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
    }

    private void updateProductViewCount(String userKey, String productKey) {
        String viewProductKey = userKey.concat(":").concat("viewProductList");

        List<String> viewProductList = redisService.getList(viewProductKey, String.class);

        if (!viewProductList.contains(productKey)) {
            redisService.leftPush(viewProductKey, productKey);

            Double currentViewCount = redisService.getZScore(VIEW_RANKING_KEY, productKey);
            Double updateViewCount =
                (currentViewCount != null) ? SCORE_INCREMENT_AMOUNT + 1 : SCORE_INCREMENT_AMOUNT;
            redisService.addZScore(VIEW_RANKING_KEY, productKey, updateViewCount);
        }
    }

    public HomeProductResponse getHomeProduct() {
        Pageable pageable = PageRequest.of(PaginationConstants.DEFAULT_PAGE,
            PaginationConstants.MIN_PAGE_SIZE);

        List<ProductResponse> goldenPriceTop5 = getProductResponseList(
            productRepository.findTop5ByGoldenPriceAsc(pageable));
        List<ProductResponse> viewCountTop5 = getProductResponseList(
            productRepository.findTop5ByViewCountDesc(pageable));
        List<ProductResponse> recentRegisteredTop5 = getProductResponseList(
            productRepository.findTop5ByIdDesc(pageable));
        List<ProductResponse> dayUseTop5 = getProductResponseList(
            productRepository.findTop5DayUseProductsCheckInDateAsc(pageable));

        return HomeProductResponse.builder()
            .goldenPriceTop5(goldenPriceTop5)
            .viewCountTop5(viewCountTop5)
            .recentRegisteredTop5(recentRegisteredTop5)
            .dayUseTop5(dayUseTop5)
            .build();
    }

    private List<ProductResponse> getProductResponseList(List<Product> productList) {
        return productList.stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public List<Product> findProductListByUserId(Long userId) {
        return productRepository.findAllByUserId(userId);
    }
}
