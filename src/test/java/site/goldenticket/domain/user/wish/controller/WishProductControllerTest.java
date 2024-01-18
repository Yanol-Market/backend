package site.goldenticket.domain.user.wish.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import site.goldenticket.common.config.ApiTest;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;
import site.goldenticket.domain.product.wish.dto.WishProductSaveRequest;
import site.goldenticket.domain.product.wish.entity.WishProduct;
import site.goldenticket.domain.product.wish.repository.WishProductRepository;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static site.goldenticket.common.utils.ProductUtils.createProduct;

@DisplayName("관심 상품 검증")
class WishProductControllerTest extends ApiTest {

    @Autowired
    private WishProductRepository wishProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("관심 상품 조회 검증")
    void getWishProducts() {
        // given
        IntStream.range(0, 3)
                .forEach(i -> {
                    Product product = saveProduct();
                    saveWishProduct(product);
                });

        String url = "/products/wish";

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(url)
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("관심 상품 등록 검증")
    void registerWishProduct() {
        // given
        Product product = saveProduct();

        WishProductSaveRequest request = new WishProductSaveRequest(product.getId());
        String url = "/products/wish";

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(url)
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.jsonPath().getLong("data.id")).isEqualTo(1L);
    }

    @Test
    @DisplayName("관심 상품 삭제 검증")
    void deleteWishProduct() {
        // given
        Product product = saveProduct();
        WishProduct wishProduct = saveWishProduct(product);

        String url = "/products/wish/" + wishProduct.getId();

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .delete(url)
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private WishProduct saveWishProduct(Product product) {
        WishProduct wishProduct = createWishProduct(product);
        wishProductRepository.save(wishProduct);
        return wishProduct;
    }

    private WishProduct createWishProduct(Product product) {
        return WishProduct.builder()
                .userId(userId)
                .product(product)
                .build();
    }

    private Product saveProduct() {
        Product product = createProduct();
        productRepository.save(product);
        return product;
    }
}
