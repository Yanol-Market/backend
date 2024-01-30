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
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
import static site.goldenticket.domain.product.constants.ProductStatus.EXPIRED;
import static site.goldenticket.domain.product.constants.ProductStatus.SOLD_OUT;

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
    @DisplayName("상품 상세 조회")
    void getProduct() {
        // given
        Product product = saveProduct();

        String url = "/products/{productId}";
        String pathName = "productId";
        Long pathValues = product.getId();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .pathParam(pathName, pathValues)
                .filter(document(
                        "products-details",
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("productId").description("상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.accommodationImage").type(STRING)
                                        .description("숙소 이미지 URL"),
                                fieldWithPath("data.accommodationName").type(STRING)
                                        .description("숙소명"),
                                fieldWithPath("data.accommodationAddress").type(STRING)
                                        .description("숙소 주소"),
                                fieldWithPath("data.reservationType").type(STRING)
                                        .description("예약 유형"),
                                fieldWithPath("data.roomName").type(STRING)
                                        .description("객실명"),
                                fieldWithPath("data.standardNumber").type(NUMBER)
                                        .description("기준 숙박 인원"),
                                fieldWithPath("data.maximumNumber").type(NUMBER)
                                        .description("최대 숙박 인원"),
                                fieldWithPath("data.checkInTime").type(STRING)
                                        .description("체크인 시간"),
                                fieldWithPath("data.checkOutTime").type(STRING)
                                        .description("체크아웃 시간"),
                                fieldWithPath("data.checkInDate").type(STRING)
                                        .description("체크인 날짜"),
                                fieldWithPath("data.checkOutDate").type(STRING)
                                        .description("체크아웃 날짜"),
                                fieldWithPath("data.nights").type(NUMBER)
                                        .description("숙박 일수"),
                                fieldWithPath("data.days").type(NUMBER)
                                        .description("판매 가능한 남은 날짜"),
                                fieldWithPath("data.originPrice").type(NUMBER)
                                        .description("구매가"),
                                fieldWithPath("data.yanoljaPrice").type(NUMBER)
                                        .description("야놀자 판매가"),
                                fieldWithPath("data.goldenPrice").type(NUMBER)
                                        .description("골든 특가"),
                                fieldWithPath("data.originPriceRatio").type(NUMBER)
                                        .description("구매 가격 할인율"),
                                fieldWithPath("data.marketPriceRatio").type(NUMBER)
                                        .description("야놀자 가격 할인율"),
                                fieldWithPath("data.content").type(STRING)
                                        .description("판매자 한마디"),
                                fieldWithPath("data.productStatus").type(STRING)
                                        .description("상품 상태"),
                                fieldWithPath("data.isSeller").type(BOOLEAN)
                                        .description("판매자 여부"),
                                fieldWithPath("data.negoProductStatus").type(NUMBER)
                                        .description("상품 네고 상태").optional(),
                                fieldWithPath("data.isWished").type(BOOLEAN)
                                        .description("관심 상품 여부")
                        )
                ))
                .when()
                .get(url, pathValues)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("상품 수정")
    void updateProduct() {
        // given
        ProductRequest request = createProductRequest();

        Product product = saveProduct();

        String url = "/products/{productId}";
        String pathName = "productId";
        Long pathValues = product.getId();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .pathParam(pathName, pathValues)
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
                                fieldWithPath("goldenPrice").type(NUMBER)
                                        .description("골든 특가"),
                                fieldWithPath("content").type(STRING)
                                        .description("판매자 한마디")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(NUMBER)
                                        .description("상품 ID")
                        )
                ))
                .when()
                .put(url, pathValues)
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
        String pathName = "productId";
        Long pathValues = product.getId();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .pathParam(pathName, pathValues)
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "products-delete",
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("productId").description("상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(NUMBER)
                                        .description("상품 ID")
                        )
                ))
                .when()
                .delete(url, pathValues)
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
                                fieldWithPath("status").type(STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(ARRAY)
                                        .description("응답 데이터"),
                                fieldWithPath("data[].productId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data[].accommodationImage").type(STRING)
                                        .description("숙소 이미지 URL"),
                                fieldWithPath("data[].accommodationName").type(STRING)
                                        .description("숙소명"),
                                fieldWithPath("data[].reservationType").type(STRING)
                                        .description("예약 유형"),
                                fieldWithPath("data[].roomName").type(STRING)
                                        .description("객실명"),
                                fieldWithPath("data[].standardNumber").type(NUMBER)
                                        .description("기준 숙박 인원"),
                                fieldWithPath("data[].maximumNumber").type(NUMBER)
                                        .description("최대 숙박 인원"),
                                fieldWithPath("data[].checkInTime").type(STRING)
                                        .description("체크인 시간"),
                                fieldWithPath("data[].checkOutTime").type(STRING)
                                        .description("체크아웃 시간"),
                                fieldWithPath("data[].checkInDate").type(STRING)
                                        .description("체크인 날짜"),
                                fieldWithPath("data[].checkOutDate").type(STRING)
                                        .description("체크아웃 날짜"),
                                fieldWithPath("data[].originPrice").type(NUMBER)
                                        .description("구매가"),
                                fieldWithPath("data[].yanoljaPrice").type(NUMBER)
                                        .description("야놀자 판매가"),
                                fieldWithPath("data[].goldenPrice").type(NUMBER)
                                        .description("골든 특가"),
                                fieldWithPath("data[].status").type(STRING)
                                        .description("판매 상태"),
                                fieldWithPath("data[].chats").type(ARRAY)
                                        .description("채팅 목록"),
                                fieldWithPath("data[].chats[].chatRoomId").type(NUMBER)
                                        .description("채팅 룸 ID"),
                                fieldWithPath("data[].chats[].receiverNickname").type(STRING)
                                        .description("구매자 닉네임"),
                                fieldWithPath("data[].chats[].receiverProfileImage").type(STRING)
                                        .description("구매자 프로필 이미지 경로").optional(),
                                fieldWithPath("data[].chats[].price").type(NUMBER)
                                        .description("거래 가격"),
                                fieldWithPath("data[].chats[].chatRoomStatus").type(STRING)
                                        .description("채팅 상태"),
                                fieldWithPath("data[].chats[].lastUpdatedAt").type(STRING)
                                        .description("채팅 최근 업데이트 시간")
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
                                fieldWithPath("status").type(STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(ARRAY)
                                        .description("응답 데이터"),
                                fieldWithPath("data[].productId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data[].accommodationImage").type(STRING)
                                        .description("숙소 이미지 URL"),
                                fieldWithPath("data[].accommodationName").type(STRING)
                                        .description("숙소명"),
                                fieldWithPath("data[].roomName").type(STRING)
                                        .description("객실명"),
                                fieldWithPath("data[].standardNumber").type(NUMBER)
                                        .description("기준 숙박 인원"),
                                fieldWithPath("data[].maximumNumber").type(NUMBER)
                                        .description("최대 숙박 인원"),
                                fieldWithPath("data[].goldenPrice").type(NUMBER)
                                        .description("골든 특가"),
                                fieldWithPath("data[].productStatus").type(STRING)
                                        .description("판매 상태")
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
        String pathName = "productId";
        Long pathValues = product.getId();
        String parameterName = "productStatus";
        String parameterValues = product.getProductStatus().toString();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .pathParam(pathName, pathValues)
                .queryParam(parameterName, parameterValues)
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "/products/history/completed-details",
                        pathParameters(
                                parameterWithName(pathName).description("상품 ID")
                        ),
                        queryParameters(
                                parameterWithName(parameterName).description("상품 상태")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("응답 메시지").optional(),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.productId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data.accommodationImage").type(STRING)
                                        .description("숙소 이미지 URL"),
                                fieldWithPath("data.accommodationName").type(STRING)
                                        .description("숙소명"),
                                fieldWithPath("data.roomName").type(STRING)
                                        .description("객실명"),
                                fieldWithPath("data.reservationType").type(STRING)
                                        .description("예약 유형"),
                                fieldWithPath("data.standardNumber").type(NUMBER)
                                        .description("기준 숙박 인원"),
                                fieldWithPath("data.maximumNumber").type(NUMBER)
                                        .description("최대 숙박 인원"),
                                fieldWithPath("data.checkInTime").type(STRING)
                                        .description("체크인 시간"),
                                fieldWithPath("data.checkOutTime").type(STRING)
                                        .description("체크아웃 시간"),
                                fieldWithPath("data.checkInDate").type(STRING)
                                        .description("체크인 날짜"),
                                fieldWithPath("data.checkOutDate").type(STRING)
                                        .description("체크아웃 날짜"),
                                fieldWithPath("data.goldenPrice").type(NUMBER)
                                        .description("골든 특가"),
                                fieldWithPath("data.completedDate").type(STRING)
                                        .description("거래 날짜"),
                                fieldWithPath("data.calculatedDate").type(STRING)
                                        .description("정산 날짜"),
                                fieldWithPath("data.fee").type(NUMBER)
                                        .description("수수료"),
                                fieldWithPath("data.calculatedPrice").type(NUMBER)
                                        .description("정산 금액"),
                                fieldWithPath("data.chatRoomId").type(NUMBER)
                                        .description("채팅 룸 ID"),
                                fieldWithPath("data.receiverNickname").type(STRING)
                                        .description("구매자 닉네임"),
                                fieldWithPath("data.receiverProfileImage").type(STRING)
                                        .description("구매자 프로필 이미지 경로").optional(),
                                fieldWithPath("data.lastUpdatedAt").type(STRING)
                                        .description("채팅 최근 업데이트 시간")
                        )
                ))
                .when()
                .get(url, pathValues, parameterValues)
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
        String pathName = "productId";
        Long pathValues = product.getId();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .pathParam(pathName, pathValues)
                .header("Authorization", "Bearer " + accessToken)
                .filter(document(
                        "products/history/completed-delete",
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("productId").description("상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(NUMBER)
                                        .description("상품 ID")
                        )
                ))
                .when()
                .delete(url, pathValues)
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
