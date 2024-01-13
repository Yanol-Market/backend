package site.goldenticket.domain.user.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.goldenticket.common.config.ApiTest;
import site.goldenticket.domain.user.dto.AgreementRequest;
import site.goldenticket.domain.user.dto.JoinRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static site.goldenticket.common.utils.UserUtils.*;

@DisplayName("UserController 검증")
class UserControllerTest extends ApiTest {

    @Test
    @DisplayName("회원가입 검증")
    void join() {
        // given
        JoinRequest request = new JoinRequest(
                NAME,
                NICKNAME,
                EMAIL,
                PASSWORD,
                PHONE_NUMBER,
                null,
                new AgreementRequest(true)
        );

        String url = "/users";

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(url)
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(CREATED.value());
        assertThat(result.jsonPath().getLong("data.id")).isEqualTo(1L);
    }
}
