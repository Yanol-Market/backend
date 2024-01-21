package site.goldenticket.common;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import site.goldenticket.common.config.ApiTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static site.goldenticket.common.response.Status.FAIL;
import static site.goldenticket.common.utils.UserUtils.EMAIL;
import static site.goldenticket.common.utils.UserUtils.PASSWORD;

@DisplayName("Security 검증")
public class SecurityTest extends ApiTest {

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        Map<String, Object> param = Map.of(
                "email", EMAIL,
                "password", PASSWORD
        );

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(param)
                .when()
                .post("/login")
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_FailureNotFoundEmail() {
        // given
        Map<String, Object> param = Map.of(
                "email", "EMAIL",
                "password", PASSWORD
        );

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(param)
                .when()
                .post("/login")
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        JsonPath jsonPath = result.jsonPath();
        assertAll(
                () -> assertThat(jsonPath.getString("status")).isEqualTo(FAIL.name()),
                () -> assertThat(jsonPath.getString("message")).isEqualTo("이메일, 비밀번호를 확인해주세요.")
        );
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 패스워드")
    void login_FailureMismatchPassword() {
        // given
        Map<String, Object> param = Map.of(
                "email", EMAIL,
                "password", "PASSWORD"
        );

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(param)
                .when()
                .post("/login")
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        JsonPath jsonPath = result.jsonPath();
        assertAll(
                () -> assertThat(jsonPath.getString("status")).isEqualTo(FAIL.name()),
                () -> assertThat(jsonPath.getString("message")).isEqualTo("이메일, 비밀번호를 확인해주세요.")
        );
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 요청")
    void login_FailureInvalidRequest() {
        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .post("/login")
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        JsonPath jsonPath = result.jsonPath();
        assertAll(
                () -> assertThat(jsonPath.getString("status")).isEqualTo(FAIL.name()),
                () -> assertThat(jsonPath.getString("message")).isEqualTo("요청한 값이 올바르지 않습니다.")
        );
    }
}
