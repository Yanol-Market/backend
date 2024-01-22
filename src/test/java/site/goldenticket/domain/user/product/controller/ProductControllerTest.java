package site.goldenticket.domain.user.product.controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import site.goldenticket.common.config.ApiTest;
import site.goldenticket.domain.chat.entity.Chat;
import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.chat.repository.ChatRepository;
import site.goldenticket.domain.chat.repository.ChatRoomRepository;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static site.goldenticket.common.utils.ProductUtils.createProduct;
import static site.goldenticket.domain.chat.entity.SenderType.BUYER;
import static site.goldenticket.domain.nego.status.NegotiationStatus.NEGOTIATING;

public class ProductControllerTest extends ApiTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private NegoRepository negoRepository;

    @Test
    void getProduct() {
        // given
        Product product = saveProduct();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/products/{productId}", product.getId())
                .then().log().all()
                .extract();

        // then
        assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()));
    }

    @Test
    void getProgressProducts() {
        // given
        Product product = saveProduct();
        ChatRoom chatRoom = saveChatRoom(product);
        saveChat(chatRoom);
        saveNego(product);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/products/history/progress")
                .then().log().all()
                .extract();

        // then
        final JsonPath result = response.jsonPath();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(result.getString("message")).isEqualTo("판매중인 상품이 성공적으로 조회가 완료되었습니다."),
                () -> assertThat(result.getString("status")).isEqualTo("SUCCESS"),
                () -> assertThat(result.getList("data.productId", Long.class)).contains(product.getId()),
                () -> assertThat(result.getList("data.chats[0].chatRoomId", Long.class)).contains(chatRoom.getId())
        );
    }

    private Product saveProduct() {
        Product product = createProduct();
        return productRepository.save(
                product
        );
    }

    private ChatRoom saveChatRoom(Product product) {
        return chatRoomRepository.save(
                ChatRoom.builder()
                        .productId(product.getId())
                        .buyerId(user.getId())
                        .build()
        );
    }

    private void saveChat(ChatRoom chatRoom) {
        chatRepository.save(
                Chat.builder()
                        .chatRoomId(chatRoom.getId())
                        .senderType(BUYER)
                        .userId(user.getId())
                        .content("message")
                        .build()
        );
    }

    private void saveNego(Product product) {
        LocalDateTime currentTime = LocalDateTime.now();

        Nego nego = Nego.builder()
                .price(50000)
                .count(0)
                .status(NEGOTIATING)
                .consent(false)
                .expirationTime(currentTime.plusDays(7))
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();

        nego.setProduct(product);
        nego.setUser(user);

        negoRepository.save(nego);
    }
}
