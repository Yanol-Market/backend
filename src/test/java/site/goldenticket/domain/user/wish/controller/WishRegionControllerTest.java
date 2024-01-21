package site.goldenticket.domain.user.wish.controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import site.goldenticket.common.config.ApiTest;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.user.wish.dto.WishRegionRegisterRequest;
import site.goldenticket.domain.user.wish.dto.WishRegionResponse;
import site.goldenticket.domain.user.wish.entity.WishRegion;
import site.goldenticket.domain.user.wish.repository.WishRegionRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static site.goldenticket.common.utils.UserUtils.createWishRegion;
import static site.goldenticket.domain.product.constants.AreaCode.*;

@DisplayName("관심 상품 검증")
class WishRegionControllerTest extends ApiTest {

    @Autowired
    private WishRegionRepository wishRegionRepository;

    @Test
    @DisplayName("관심 지역 등록 검증")
    void registerWishRegion() {
        // given
        WishRegionRegisterRequest request = new WishRegionRegisterRequest(List.of(SEOUL, BUSAN, DAEGU));

        String url = "/users/regions";

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
        assertThat(result.statusCode()).isEqualTo(OK.value());
    }

    @Test
    @DisplayName("관심 지역 조회 검증")
    void getWishRegion() {
        // given
        List.of(SEOUL, BUSAN, DAEGU).forEach(this::saveWishRegion);

        String url = "/users/regions";

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(url)
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(OK.value());

        JsonPath jsonPath = result.jsonPath();
        assertThat(jsonPath.getList("data.wishRegions", WishRegionResponse.class).size()).isEqualTo(3);
    }

    private void saveWishRegion(AreaCode areaCode) {
        WishRegion wishRegion = createWishRegion(areaCode);
        wishRegion.registerUser(user);
        wishRegionRepository.save(wishRegion);
    }
}
