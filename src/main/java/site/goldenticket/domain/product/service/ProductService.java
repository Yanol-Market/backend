package site.goldenticket.domain.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import site.goldenticket.common.redis.service.RedisService;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.dto.*;
import site.goldenticket.domain.product.repository.CustomSlice;
import site.goldenticket.dummy.reservation.constants.ReservationStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;
import site.goldenticket.domain.security.PrincipalDetails;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static site.goldenticket.common.redis.constants.RedisConstants.*;
import static site.goldenticket.common.response.ErrorCode.*;
import static site.goldenticket.domain.product.constants.DummyUrlConstants.*;
import static site.goldenticket.dummy.reservation.constants.ReservationStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private static final String ANONYMOUS_KEY_COOKIE_NAME = "AnonymousKey";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private final RedisService redisService;
    private final ProductRepository productRepository;

    // 1. 키워드 검색 및 지역 검색 메서드
    @Transactional(readOnly = true)
    public Slice<SearchProductResponse> getProductsBySearch(
            AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate, PriceRange priceRange,
            LocalDate cursorCheckInDate, Long cursorId, Pageable pageable
    ) {
        CustomSlice<Product> productSlice = productRepository.getProductsBySearch(
                areaCode, keyword, checkInDate, checkOutDate, priceRange, cursorCheckInDate, cursorId, pageable
        );

        SearchProductResponse searchProductResponse = SearchProductResponse.fromEntity(
                areaCode, keyword, checkInDate, checkOutDate, priceRange, productSlice.getTotalElements(), productSlice
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

    // 2. 야놀자 예약 정보 조회 메서드
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations(Long yaUserId) {
        URI targetUrl = UriComponentsBuilder
                .fromUriString(LOCAL_BASE_URL)
                .path(RESERVATIONS_ENDPOINT)
                .buildAndExpand(yaUserId)
                .encode(UTF_8)
                .toUri();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(targetUrl, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            try {
                JsonNode dataNode = objectMapper.readTree(responseBody).get("data");

                List<ReservationResponse> reservationProductResponseList = new ArrayList<>();
                if (dataNode.isArray() && !dataNode.isEmpty()) {
                    for (JsonNode reservationNode : dataNode) {
                        ReservationResponse reservationProductResponse = objectMapper.treeToValue(reservationNode, ReservationResponse.class);

                        Long reservationId = reservationProductResponse.getReservationId();
                        ReservationStatus reservationStatus = productRepository.existsByReservationId(reservationId) ? REGISTERED : NOT_REGISTERED;

                        ReservationResponse updatedReservationProductResponse = reservationProductResponse.toBuilder()
                                .reservationStatus(reservationStatus)
                                .build();

                        reservationProductResponseList.add(updatedReservationProductResponse);
                    }
                } else {
                    return Collections.emptyList();
                }
                return reservationProductResponseList;
            } catch (JsonProcessingException e) {
                throw new CustomException(COMMON_JSON_PROCESSING_ERROR);
            }
        } else {
            throw new CustomException(RESERVATION_INTERNAL_SERVER_ERROR);
        }
    }

    // 3. 상품 생성, 조회, 수정, 삭제 관련 메서드
    @Transactional
    public ProductResponse createProduct(
            ProductRequest productRequest, Long reservationId, PrincipalDetails principalDetails
    ) {
        if (productRepository.existsByReservationId(reservationId)) {
            throw new CustomException(PRODUCT_ALREADY_EXISTS);
        }

        URI targetUrl = UriComponentsBuilder
                .fromUriString(LOCAL_BASE_URL)
                .path(RESERVATION_ENDPOINT)
                .buildAndExpand(reservationId)
                .encode(UTF_8)
                .toUri();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(targetUrl, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            try {
                JsonNode reservationNode = objectMapper.readTree(responseBody).get("data");

                ReservationDetailsResponse reservationDetailsResponse = objectMapper.treeToValue(reservationNode, ReservationDetailsResponse.class);

                Product product = productRequest.toEntity(reservationDetailsResponse, principalDetails.getUserId());
                Product savedProduct = productRepository.save(product);

                return ProductResponse.fromEntity(savedProduct);
            } catch (JsonProcessingException e) {
                throw new CustomException(COMMON_JSON_PROCESSING_ERROR);
            }
        } else {
            throw new CustomException(RESERVATION_INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(
            Long productId, PrincipalDetails principalDetails, HttpServletRequest request, HttpServletResponse response
    ) {
        Product product = getProduct(productId);

        String userKey = (principalDetails != null) ? principalDetails.getUsername() : generateOrRetrieveAnonymousKey(request, response);
        boolean isSeller = principalDetails != null && principalDetails.getUserId().equals(product.getUserId());

        updateProductViewCount(userKey, productId.toString());

        return ProductDetailResponse.fromEntity(product, isSeller);
    }

    @Transactional
    public ProductResponse updateProduct(ProductRequest productRequest, Long productId) {
        Product product = getProduct(productId);
        product.update(productRequest.goldenPrice(), productRequest.content());
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
    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }

    private String generateOrRetrieveAnonymousKey(HttpServletRequest request, HttpServletResponse response) {
        String anonymousKey = Arrays.stream(request.getCookies())
                .filter(
                        cookie -> ANONYMOUS_KEY_COOKIE_NAME.equals(cookie.getName())
                )
                .map(
                        cookie -> cookie.getValue()
                )
                .findFirst()
                .orElse(null);

        if (anonymousKey == null) {
            ResponseCookie cookie = ResponseCookie.from(ANONYMOUS_KEY_COOKIE_NAME, UUID.randomUUID().toString())
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
            Double updateViewCount = (currentViewCount != null) ? SCORE_INCREMENT_AMOUNT + 1 : SCORE_INCREMENT_AMOUNT;
            redisService.addZScore(VIEW_RANKING_KEY, productKey, updateViewCount);
        }
    }

    public HomeProductResponse getHomeProduct() {
        Pageable pageable = PageRequest.of(PaginationConstants.DEFAULT_PAGE, PaginationConstants.MIN_PAGE_SIZE);

        List<ProductResponse> goldenPriceTop5 = getProductResponseList(productRepository.findTop5ByGoldenPriceAsc(pageable));
        List<ProductResponse> viewCountTop5 = getProductResponseList(productRepository.findTop5ByViewCountDesc(pageable));
        List<ProductResponse> recentRegisteredTop5 = getProductResponseList(productRepository.findTop5ByIdDesc(pageable));
        List<ProductResponse> dayUseTop5 = getProductResponseList(productRepository.findTop5DayUseProductsCheckInDateAsc(pageable));

        return new HomeProductResponse(
                goldenPriceTop5,
                viewCountTop5,
                recentRegisteredTop5,
                dayUseTop5
        );
    }

    private List<ProductResponse> getProductResponseList(List<Product> productList) {
        return productList.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
