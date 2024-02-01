package site.goldenticket.domain.product.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import site.goldenticket.common.config.ApiDocumentation;
import site.goldenticket.domain.chat.entity.Chat;
import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.chat.repository.ChatRepository;
import site.goldenticket.domain.chat.repository.ChatRoomRepository;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.repository.OrderRepository;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static site.goldenticket.common.utils.ChatRoomUtils.createChatRoom;
import static site.goldenticket.common.utils.ChatUtils.createChat;
import static site.goldenticket.common.utils.NegoUtils.createNego;
import static site.goldenticket.common.utils.OrderUtils.createOrder;
import static site.goldenticket.common.utils.ProductUtils.createProduct;
import static site.goldenticket.common.utils.ProductUtils.createProductRequest;
import static site.goldenticket.domain.product.constants.PriceRange.BETWEEN_10_AND_20;
import static site.goldenticket.domain.product.constants.ProductStatus.EXPIRED;
import static site.goldenticket.domain.product.constants.ProductStatus.SOLD_OUT;

@DisplayName("ProductController 검증")
public class ProductControllerTest extends ApiDocumentation {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private NegoRepository negoRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("상품 검색 조회")
    void getProductsBySearch() {
        // given
        Product product = saveProduct();

        String url = "/products?areaCode={areaCode}&keyword={keyword}&checkInDate={checkInDate}" +
                "&checkOutDate={checkOutDate}&priceRange={priceRange}&cursorId={cursorId}" +
                "&cursorCheckInDate={cursorCheckInDate}";

        AreaCode areaCode = product.getAreaCode();
        String accommodationName = product.getAccommodationName();
        String checkInDate = String.valueOf(product.getCheckInDate());
        String checkOutDate = String.valueOf(product.getCheckOutDate());
        PriceRange priceRange = BETWEEN_10_AND_20;
        Long cursorId = 0L;
        String cursorCheckInDate = String.valueOf(product.getCheckInDate().minusDays(1));

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .filter(document(
                        "products-search",
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("areaCode").description("지역명"),
                                parameterWithName("keyword").description("숙소명"),
                                parameterWithName("checkInDate").description("체크인 날짜"),
                                parameterWithName("checkOutDate").description("체크아웃 날짜"),
                                parameterWithName("priceRange").description("가격 범위"),
                                parameterWithName("cursorId").description("커서 아이디"),
                                parameterWithName("cursorCheckInDate").description("커서 체크인 날짜")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data.content").type(LIST).description("컨텐츠 리스트"),
                                fieldWithPath("data.pageable").type(OBJECT).description("페이징 정보"),
                                fieldWithPath("data.first").type(BOOLEAN).description("첫 번째 페이지 여부"),
                                fieldWithPath("data.last").type(BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("data.size").type(NUMBER).description("페이지 크기"),
                                fieldWithPath("data.number").type(NUMBER).description("페이지 번호"),
                                fieldWithPath("data.numberOfElements").type(NUMBER).description("현재 페이지의 요소 수"),
                                fieldWithPath("data.empty").type(BOOLEAN).description("데이터가 비어 있는지 여부"),

                                // pageable
                                fieldWithPath("data.pageable.pageNumber").type(NUMBER).description("페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").type(NUMBER).description("페이지 크기"),
                                fieldWithPath("data.pageable.sort").type(OBJECT).description("정렬 정보"),
                                fieldWithPath("data.pageable.sort.empty").type(BOOLEAN).description("정렬 정보가 비어 있는지 여부"),
                                fieldWithPath("data.pageable.sort.sorted").type(BOOLEAN).description("정렬 정보가 정렬되어 있는지 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").type(BOOLEAN).description("정렬 정보가 정렬되어 있지 않은지 여부"),
                                fieldWithPath("data.pageable.offset").type(NUMBER).description("오프셋"),
                                fieldWithPath("data.pageable.paged").type(BOOLEAN).description("페이징 여부"),
                                fieldWithPath("data.pageable.unpaged").type(BOOLEAN).description("페이징되지 않은 경우 여부"),

                                // sort fields
                                fieldWithPath("data.sort.empty").type(BOOLEAN).description("정렬 정보가 비어 있는지 여부"),
                                fieldWithPath("data.sort.sorted").type(BOOLEAN).description("정렬 정보가 정렬되어 있는지 여부"),
                                fieldWithPath("data.sort.unsorted").type(BOOLEAN).description("정렬 정보가 정렬되어 있지 않은지 여부"),

                                // content fields
                                fieldWithPath("data.content[0].areaName").type(STRING).description("지역명"),
                                fieldWithPath("data.content[0].keyword").type(STRING).description("검색어"),
                                fieldWithPath("data.content[0].checkInDate").type(STRING).description("체크인 날짜"),
                                fieldWithPath("data.content[0].checkOutDate").type(STRING).description("체크아웃 날짜"),
                                fieldWithPath("data.content[0].priceRange").type(STRING).description("가격대"),
                                fieldWithPath("data.content[0].totalCount").type(NUMBER).description("총 개수"),

                                // productResponseList
                                fieldWithPath("data.content[0].wishedProductResponseList").type(LIST).description("상품 응답 리스트"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].productId").type(NUMBER).description("상품 ID"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].accommodationImage").type(STRING).description("숙소 이미지"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].accommodationName").type(STRING).description("숙소명"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].reservationType").type(STRING).description("예약 유형"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].roomName").type(STRING).description("객실명"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].checkInDate").type(STRING).description("체크인 날짜"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].checkOutDate").type(STRING).description("체크아웃 날짜"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].nights").type(NUMBER).description("숙박 일수"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].days").type(NUMBER).description("판매 종료 까지 남은 일 수"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].originPrice").type(NUMBER).description("원래 가격"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].yanoljaPrice").type(NUMBER).description("야놀자 가격"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].goldenPrice").type(NUMBER).description("골든 가격"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].originPriceRatio").type(NUMBER).description("구매가 대비 할인율"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].marketPriceRatio").type(NUMBER).description("야놀자 판매가 대비 할인율"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].productStatus").type(STRING).description("상품 상태"),
                                fieldWithPath("data.content[0].wishedProductResponseList[0].isWished").type(BOOLEAN).description("찜 여부")
                        )
                ))
                .when()
                .get(
                        url,
                        areaCode,
                        accommodationName,
                        checkInDate,
                        checkOutDate,
                        priceRange,
                        cursorId,
                        cursorCheckInDate
                        )
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("예약 목록 조회")
    void getAllReservations() {
        // given
        String url = "/products/reservations/{yaUserId}";
        Long yaUserId = -1L;

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "reservations-all",
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("yaUserId").description("야놀자 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data[].reservationId").type(NUMBER).description("예약 ID"),
                                fieldWithPath("data[].accommodationName").type(STRING).description("숙소명"),
                                fieldWithPath("data[].reservationType").type(STRING).description("예약 유형"),
                                fieldWithPath("data[].roomName").type(STRING).description("객실명"),
                                fieldWithPath("data[].standardNumber").type(NUMBER).description("기준 숙박 인원"),
                                fieldWithPath("data[].maximumNumber").type(NUMBER).description("최대 숙박 인원"),
                                fieldWithPath("data[].checkInDate").type(STRING).description("체크인 날짜"),
                                fieldWithPath("data[].checkOutDate").type(STRING).description("체크아웃 날짜"),
                                fieldWithPath("data[].checkInTime").type(STRING).description("체크인 시간"),
                                fieldWithPath("data[].checkOutTime").type(STRING).description("체크아웃 시간"),
                                fieldWithPath("data[].nights").type(NUMBER).description("숙박 일수"),
                                fieldWithPath("data[].reservationDate").type(STRING).description("예약일"),
                                fieldWithPath("data[].originPrice").type(NUMBER).description("구매가"),
                                fieldWithPath("data[].yanoljaPrice").type(NUMBER).description("야놀자 판매가"),
                                fieldWithPath("data[].reservationStatus").type(STRING).description("예약 상태")
                        )
                ))
                .when()
                .get(url, yaUserId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("상품 등록")
    void registerProduct() {
        // given
        ProductRequest request = createProductRequest();

        String url = "/products/{reservationId}";
        Long reservationId = 1L;

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "product-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("reservationId").description("예약 ID")
                        ),
                        requestFields(
                                fieldWithPath("goldenPrice").type(NUMBER).description("골든 특가"),
                                fieldWithPath("content").type(STRING).description("판매자 한마디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data.productId").type(NUMBER).description("상품 ID"),
                                fieldWithPath("data.accommodationImage").type(STRING).description("숙소 이미지 URL"),
                                fieldWithPath("data.accommodationName").type(STRING).description("숙소명"),
                                fieldWithPath("data.reservationType").type(STRING).description("예약 유형"),
                                fieldWithPath("data.roomName").type(STRING).description("객실명"),
                                fieldWithPath("data.checkInDate").type(STRING).description("체크인 날짜"),
                                fieldWithPath("data.checkOutDate").type(STRING).description("체크아웃 날짜"),
                                fieldWithPath("data.nights").type(NUMBER).description("숙박 일수"),
                                fieldWithPath("data.days").type(NUMBER).description("판매 가능한 남은 날짜"),
                                fieldWithPath("data.originPrice").type(NUMBER).description("구매가"),
                                fieldWithPath("data.yanoljaPrice").type(NUMBER).description("야놀자 판매가"),
                                fieldWithPath("data.goldenPrice").type(NUMBER).description("골든 특가"),
                                fieldWithPath("data.productStatus").type(STRING).description("상품 상태")
                        )
                ))
                .when()
                .post(url, reservationId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("상품 상세 조회")
    void getProduct() {
        // given
        Product product = saveProduct();

        String url = "/products/{productId}";
        Long productId = product.getId();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .filter(document(
                        "products-details",
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("productId").description("상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.accommodationImage").type(STRING).description("숙소 이미지 URL"),
                                fieldWithPath("data.accommodationName").type(STRING).description("숙소명"),
                                fieldWithPath("data.accommodationAddress").type(STRING).description("숙소 주소"),
                                fieldWithPath("data.reservationType").type(STRING).description("예약 유형"),
                                fieldWithPath("data.roomName").type(STRING).description("객실명"),
                                fieldWithPath("data.standardNumber").type(NUMBER).description("기준 숙박 인원"),
                                fieldWithPath("data.maximumNumber").type(NUMBER).description("최대 숙박 인원"),
                                fieldWithPath("data.checkInTime").type(STRING).description("체크인 시간"),
                                fieldWithPath("data.checkOutTime").type(STRING).description("체크아웃 시간"),
                                fieldWithPath("data.checkInDate").type(STRING).description("체크인 날짜"),
                                fieldWithPath("data.checkOutDate").type(STRING).description("체크아웃 날짜"),
                                fieldWithPath("data.nights").type(NUMBER).description("숙박 일수"),
                                fieldWithPath("data.days").type(NUMBER).description("판매 가능한 남은 날짜"),
                                fieldWithPath("data.originPrice").type(NUMBER).description("구매가"),
                                fieldWithPath("data.yanoljaPrice").type(NUMBER).description("야놀자 판매가"),
                                fieldWithPath("data.goldenPrice").type(NUMBER).description("골든 특가"),
                                fieldWithPath("data.originPriceRatio").type(NUMBER).description("구매 가격 할인율"),
                                fieldWithPath("data.marketPriceRatio").type(NUMBER).description("야놀자 가격 할인율"),
                                fieldWithPath("data.content").type(STRING).description("판매자 한마디"),
                                fieldWithPath("data.productStatus").type(STRING).description("상품 상태"),
                                fieldWithPath("data.isSeller").type(BOOLEAN).description("판매자 여부"),
                                fieldWithPath("data.negoProductStatus").type(NUMBER).description("상품 네고 상태").optional(),
                                fieldWithPath("data.isWished").type(BOOLEAN).description("관심 상품 여부")
                        )
                ))
                .when()
                .get(url, productId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("상품 수정")
    void updateProduct() {
        // given
        Product product = saveProduct();
        ProductRequest request = createProductRequest();

        String url = "/products/{productId}";
        Long productId = product.getId();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "products-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("productId").description("상품 ID")
                        ),
                        requestFields(
                                fieldWithPath("goldenPrice").type(NUMBER).description("골든 특가"),
                                fieldWithPath("content").type(STRING).description("판매자 한마디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data").type(NUMBER).description("상품 ID")
                        )
                ))
                .when()
                .put(url, productId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    @Test
    @DisplayName("상품 삭제")
    void deleteProduct() {
        // given
        Product product = saveProduct();

        String url = "/products/{productId}";
        Long productId = product.getId();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "products-delete",
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("productId").description("상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data").type(NUMBER).description("상품 ID")
                        )
                ))
                .when()
                .delete(url, productId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("판매 내역 - 판매중 전체 조회")
    void getProgressProducts() {
        // given
        Product product = saveProduct();
        ChatRoom chatRoom = saveChatRoom(product);
        saveChat(chatRoom);
        saveNego(product);

        String url = "/products/history/progress";

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "products/history/progress-all",
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data").type(ARRAY).description("응답 데이터"),
                                fieldWithPath("data[].productId").type(NUMBER).description("상품 ID"),
                                fieldWithPath("data[].accommodationImage").type(STRING).description("숙소 이미지 URL"),
                                fieldWithPath("data[].accommodationName").type(STRING).description("숙소명"),
                                fieldWithPath("data[].reservationType").type(STRING).description("예약 유형"),
                                fieldWithPath("data[].roomName").type(STRING).description("객실명"),
                                fieldWithPath("data[].standardNumber").type(NUMBER).description("기준 숙박 인원"),
                                fieldWithPath("data[].maximumNumber").type(NUMBER).description("최대 숙박 인원"),
                                fieldWithPath("data[].checkInTime").type(STRING).description("체크인 시간"),
                                fieldWithPath("data[].checkOutTime").type(STRING).description("체크아웃 시간"),
                                fieldWithPath("data[].checkInDate").type(STRING).description("체크인 날짜"),
                                fieldWithPath("data[].checkOutDate").type(STRING).description("체크아웃 날짜"),
                                fieldWithPath("data[].originPrice").type(NUMBER).description("구매가"),
                                fieldWithPath("data[].yanoljaPrice").type(NUMBER).description("야놀자 판매가"),
                                fieldWithPath("data[].goldenPrice").type(NUMBER).description("골든 특가"),
                                fieldWithPath("data[].status").type(STRING).description("판매 상태"),
                                fieldWithPath("data[].chats").type(ARRAY).description("채팅 목록"),
                                fieldWithPath("data[].chats[].chatRoomId").type(NUMBER).description("채팅 룸 ID"),
                                fieldWithPath("data[].chats[].receiverNickname").type(STRING).description("구매자 닉네임"),
                                fieldWithPath("data[].chats[].receiverProfileImage").type(STRING).description("구매자 프로필 이미지 경로").optional(),
                                fieldWithPath("data[].chats[].price").type(NUMBER).description("거래 가격"),
                                fieldWithPath("data[].chats[].chatRoomStatus").type(STRING).description("채팅 상태"),
                                fieldWithPath("data[].chats[].lastUpdatedAt").type(STRING).description("채팅 최근 업데이트 시간")
                        )
                ))
                .when()
                .get(url)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("판매 내역 - 판매 완료 전체 조회")
    void getAllCompletedProducts() {
        // given
        createCompletedProductsList();

        String url = "/products/history/completed";

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "products/history/completed-all",
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data").type(ARRAY).description("응답 데이터"),
                                fieldWithPath("data[].productId").type(NUMBER).description("상품 ID"),
                                fieldWithPath("data[].accommodationImage").type(STRING).description("숙소 이미지 URL"),
                                fieldWithPath("data[].accommodationName").type(STRING).description("숙소명"),
                                fieldWithPath("data[].roomName").type(STRING).description("객실명"),
                                fieldWithPath("data[].standardNumber").type(NUMBER).description("기준 숙박 인원"),
                                fieldWithPath("data[].maximumNumber").type(NUMBER).description("최대 숙박 인원"),
                                fieldWithPath("data[].goldenPrice").type(NUMBER).description("골든 특가"),
                                fieldWithPath("data[].productStatus").type(STRING).description("판매 상태")
                        )
                ))
                .when()
                .get(url)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("판매 내역 - 판매 완료 상세 조회")
    void getCompletedProductDetails() {
        // given
        Product product = saveSoldOutProduct();
        ChatRoom chatRoom = saveChatRoom(product);
        saveChat(chatRoom);
        saveOrder(product);

        String url = "/products/history/completed/{productId}?productStatus={productStatus}";
        Long productId = product.getId();
        String productStatus = product.getProductStatus().toString();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "products/history/completed-details",
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("productId").description("상품 ID")
                        ),
                        queryParameters(
                                parameterWithName("productStatus").description("상품 상태")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지").optional(),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.productId").type(NUMBER).description("상품 ID"),
                                fieldWithPath("data.accommodationImage").type(STRING).description("숙소 이미지 URL"),
                                fieldWithPath("data.accommodationName").type(STRING).description("숙소명"),
                                fieldWithPath("data.roomName").type(STRING).description("객실명"),
                                fieldWithPath("data.reservationType").type(STRING).description("예약 유형"),
                                fieldWithPath("data.standardNumber").type(NUMBER).description("기준 숙박 인원"),
                                fieldWithPath("data.maximumNumber").type(NUMBER).description("최대 숙박 인원"),
                                fieldWithPath("data.checkInTime").type(STRING).description("체크인 시간"),
                                fieldWithPath("data.checkOutTime").type(STRING).description("체크아웃 시간"),
                                fieldWithPath("data.checkInDate").type(STRING).description("체크인 날짜"),
                                fieldWithPath("data.checkOutDate").type(STRING).description("체크아웃 날짜"),
                                fieldWithPath("data.goldenPrice").type(NUMBER).description("골든 특가"),
                                fieldWithPath("data.completedDate").type(STRING).description("거래 날짜"),
                                fieldWithPath("data.calculatedDate").type(STRING).description("정산 날짜"),
                                fieldWithPath("data.fee").type(NUMBER).description("수수료"),
                                fieldWithPath("data.calculatedPrice").type(NUMBER).description("정산 금액"),
                                fieldWithPath("data.chatRoomId").type(NUMBER).description("채팅 룸 ID"),
                                fieldWithPath("data.receiverNickname").type(STRING).description("구매자 닉네임"),
                                fieldWithPath("data.receiverProfileImage").type(STRING).description("구매자 프로필 이미지 경로").optional(),
                                fieldWithPath("data.lastUpdatedAt").type(STRING).description("채팅 최근 업데이트 시간")
                        )
                ))
                .when()
                .get(url, productId, productStatus)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("판매 내역 - 판매 완료 삭제")
    void deleteCompletedProduct(){
        // given
        Product product = saveSoldOutProduct();

        String url = "/products/history/completed/{productId}";
        Long productId = product.getId();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "products/history/completed-delete",
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("productId").description("상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data").type(NUMBER).description("상품 ID")
                        )
                ))
                .when()
                .delete(url, productId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private Product saveProduct() {
        Product product = createProduct();
        return productRepository.save(product);
    }

    private Product saveSoldOutProduct() {
        Product product = createProduct();
        product.setProductStatus(SOLD_OUT);
        return productRepository.save(product);
    }

    private Product saveExpiredProduct() {
        Product product = createProduct();
        product.setProductStatus(EXPIRED);
        return productRepository.save(product);
    }

    private void createCompletedProductsList() {
        List.of(saveSoldOutProduct(), saveExpiredProduct());
    }

    private ChatRoom saveChatRoom(Product product) {
        ChatRoom chatRoom = createChatRoom(product, user);
        return chatRoomRepository.save(chatRoom);
    }

    private void saveChat(ChatRoom chatRoom) {
        Chat chat = createChat(chatRoom, user);
        chatRepository.save(chat);
    }

    private void saveNego(Product product) {
        Nego nego = createNego(product, user);
        negoRepository.save(nego);
    }

    private void saveOrder(Product product) {
        Order order = createOrder(product, user);
        orderRepository.save(order);
    }
}
