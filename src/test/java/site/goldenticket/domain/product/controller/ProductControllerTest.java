package site.goldenticket.domain.product.controller;

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
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.repository.OrderRepository;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static site.goldenticket.common.constants.OrderStatus.WAITING_TRANSFER;
import static site.goldenticket.common.utils.ProductUtils.createProduct;
import static site.goldenticket.domain.chat.entity.SenderType.BUYER;
import static site.goldenticket.domain.nego.status.NegotiationStatus.NEGOTIATING;
import static site.goldenticket.domain.nego.status.NegotiationStatus.TRANSFER_PENDING;
import static site.goldenticket.domain.product.constants.ProductStatus.EXPIRED;
import static site.goldenticket.domain.product.constants.ProductStatus.SOLD_OUT;

public class ProductControllerTest extends ApiTest {

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
    void getProduct() {
        // given
        Product product = saveProduct();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/products/{productId}", product.getId())
                .then().log().all()
                .extract();

        // then
        final JsonPath result = response.jsonPath();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(result.getLong("data.productId")).isEqualTo(product.getId())
        );
    }

    @Test
    void deleteProduct() {
        // given
        Product product = saveProduct();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/products/{productId}", product.getId())
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
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
                () -> assertThat(result.getList("data.productId", Long.class)).contains(product.getId()),
                () -> assertThat(result.getList("data.chats[0].chatRoomId", Long.class)).contains(chatRoom.getId())
        );
    }

    @Test
    void getAllCompletedProducts() {
        // given
        List<Product> products = createCompletedProductsList();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/products/history/completed")
                .then().log().all()
                .extract();

        // then
        final JsonPath result = response.jsonPath();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(result.getLong("data[0].productId")).isEqualTo(products.get(0).getId().intValue()),
                () -> assertThat(result.getLong("data[1].productId")).isEqualTo(products.get(1).getId().intValue())
        );
    }

    @Test
    void getCompletedProductDetails() {
        // given
        Product product = saveSoldOutProduct();
        ChatRoom chatRoom = saveChatRoom(product);
        saveChat(chatRoom);
        saveOrder(product);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("productStatus", product.getProductStatus())
                .when().get("/products/history/completed/{productId}", product.getId())
                .then().log().all()
                .extract();

        // then
        final JsonPath result = response.jsonPath();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(result.getLong("data.productId")).isEqualTo(product.getId())
        );
    }

    @Test
    void deleteCompletedProduct(){
        // given
        Product product = saveSoldOutProduct();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/products/history/completed/{productId}", product.getId())
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

    }

    private Product saveProduct() {
        Product product = createProduct();
        return productRepository.save(
                product
        );
    }

    private Product saveSoldOutProduct() {
        Product product = createProduct();
        product.setProductStatus(SOLD_OUT);
        return productRepository.save(
                product
        );
    }

    private Product saveExpiredProduct() {
        Product product = createProduct();
        product.setProductStatus(EXPIRED);
        return productRepository.save(
                product
        );
    }

    private List<Product> createCompletedProductsList() {
        return List.of(saveSoldOutProduct(), saveExpiredProduct());
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

    private void saveOrder(Product product) {
        Order order = Order.builder()
                .productId(product.getId())
                .userId(user.getId())
                .status(WAITING_TRANSFER)
                .negoStatus(TRANSFER_PENDING)
                .price(50000)
                .build();

        orderRepository.save(order);
    }
}
