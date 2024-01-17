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
import site.goldenticket.domain.user.wish.dto.WishProductSaveRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static site.goldenticket.common.utils.ProductUtils.createProduct;

@DisplayName("관심 상품 검증")
class WishControllerTest extends ApiTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("관심 상품 등록 검증")
    void registerWishProduct() {
        // given
        Product product = saveProduct();

        WishProductSaveRequest request = new WishProductSaveRequest(product.getId());
        String url = "/users/wishes/product";

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

    private Product saveProduct() {
        Product product = createProduct();
        productRepository.save(product);
        return product;
    }
}
