package site.goldenticket.domain.product.controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static site.goldenticket.common.utils.ChatRoomUtils.createChatRoom;
import static site.goldenticket.common.utils.ChatUtils.createChat;
import static site.goldenticket.common.utils.NegoUtils.createNego;
import static site.goldenticket.common.utils.OrderUtils.createOrder;
import static site.goldenticket.common.utils.ProductUtils.createProduct;
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

        String url = "/products/" + product.getId();

        // when
        final ExtractableResponse<Response> response = performGetRequest(url, false);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void deleteProduct() {
        // given
        Product product = saveProduct();

        String url = "/products/" + product.getId();

        // when
        final ExtractableResponse<Response> response = performDeleteRequest(url);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void getProgressProducts() {
        // given
        Product product = saveProduct();
        ChatRoom chatRoom = saveChatRoom(product);
        saveChat(chatRoom);
        saveNego(product);

        String url = "/products/history/progress";

        // when
        final ExtractableResponse<Response> response = performGetRequest(url, true);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final JsonPath result = response.jsonPath();
        assertAll(
                () -> assertThat(result.getList("data.productId", Long.class)).contains(product.getId()),
                () -> assertThat(result.getList("data.chats[0].chatRoomId", Long.class)).contains(chatRoom.getId())
        );
    }

    @Test
    void getAllCompletedProducts() {
        // given
        List<Product> products = createCompletedProductsList();

        String url = "/products/history/completed";

        // when
        final ExtractableResponse<Response> response = performGetRequest(url, true);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final JsonPath result = response.jsonPath();
        assertAll(
                () -> assertThat(result.getLong("data[0].productId")).isEqualTo(products.get(0).getId().intValue()),
                () -> assertThat(result.getLong("data[1].productId")).isEqualTo(products.get(1).getId().intValue())
        );
    }

    @Test
    void getCompletedProductDetails() {
        // given
        Product product = saveExpiredProduct();
        ChatRoom chatRoom = saveChatRoom(product);
        saveChat(chatRoom);
        saveOrder(product);

        String url = "/products/history/completed/" + product.getId();
        String parameterName = "productStatus";
        String parameterValues = EXPIRED.toString();

        // when
        final ExtractableResponse<Response> response = performGetRequestWithQueryParam(url, parameterName, parameterValues, true);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final JsonPath result = response.jsonPath();
        assertThat(result.getLong("data.productId")).isEqualTo(product.getId());
    }

    @Test
    void deleteCompletedProduct(){
        // given
        Product product = saveSoldOutProduct();

        String url = "/products/history/completed/" + product.getId();

        // when
        final ExtractableResponse<Response> response = performDeleteRequest(url);

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

    private List<Product> createCompletedProductsList() {
        return List.of(saveSoldOutProduct(), saveExpiredProduct());
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

    private RequestSpecification setupRequestSpecification(boolean needsAuthentication) {
        RequestSpecification requestSpecification = RestAssured.given().log().all();

        if (needsAuthentication) {
            requestSpecification.header("Authorization", "Bearer " + accessToken);
        }

        return requestSpecification;
    }

    private ExtractableResponse<Response> performGetRequest(String url, boolean needsAuthentication) {
        RequestSpecification requestSpecification = setupRequestSpecification(needsAuthentication);

        return requestSpecification
                .when().get(url)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> performGetRequestWithQueryParam(String url, String parameterName, String parameterValues, boolean needsAuthentication) {
        RequestSpecification requestSpecification = setupRequestSpecification(needsAuthentication);

        return requestSpecification
                .queryParam(parameterName, parameterValues)
                .when().get(url)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> performDeleteRequest(String url) {
        RequestSpecification requestSpecification = setupRequestSpecification(true);

        return requestSpecification
                .when().delete(url)
                .then().log().all()
                .extract();
    }
}
