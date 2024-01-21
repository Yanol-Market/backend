package site.goldenticket.domain.user.controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import site.goldenticket.common.config.ApiTest;
import site.goldenticket.domain.user.dto.AgreementRequest;
import site.goldenticket.domain.user.dto.ChangePasswordRequest;
import site.goldenticket.domain.user.dto.JoinRequest;
import site.goldenticket.domain.user.dto.RegisterAccountRequest;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static site.goldenticket.common.utils.UserUtils.*;

@DisplayName("UserController 검증")
class UserControllerTest extends ApiTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 검증")
    void join() {
        // given
        JoinRequest request = new JoinRequest(
                NAME,
                "NICKNAME",
                "join@gmail.com",
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
    }

    @Test
    @DisplayName("사용자 정보 조회 검증")
    void getUserInfo() {
        // given
        String url = "/users/me";

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
        assertAll(
                () -> assertThat(jsonPath.getLong("data.id")).isEqualTo(user.getId()),
                () -> assertThat(jsonPath.getString("data.email")).isEqualTo(EMAIL),
                () -> assertThat(jsonPath.getString("data.name")).isEqualTo(NAME),
                () -> assertThat(jsonPath.getString("data.nickname")).isEqualTo(NICKNAME),
                () -> assertThat(jsonPath.getString("data.imageUrl")).isNull(),
                () -> assertThat(jsonPath.getString("data.phoneNumber")).isEqualTo(PHONE_NUMBER),
                () -> assertThat(jsonPath.getLong("data.id")).isEqualTo(YANOLJA_ID)
        );
    }

    @Test
    @DisplayName("비밀번호 변경 검증")
    void changePassword() {
        // given
        ChangePasswordRequest request = new ChangePasswordRequest(PASSWORD, CHANGE_PASSWORD);

        String url = "/users/password";

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .patch(url)
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(OK.value());

        User findUser = userRepository.findById(this.user.getId()).get();
        assertThat(findUser.getPassword()).isEqualTo(CHANGE_PASSWORD);
    }

    @Test
    @DisplayName("계좌 등록 검증")
    void registerAccount() {
        // given
        RegisterAccountRequest request = new RegisterAccountRequest(
                BANK_NAME,
                ACCOUNT_NUMBER
        );

        String url = "/users/account";

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .patch(url)
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(OK.value());
    }

    @Test
    @DisplayName("계좌 조회 검증")
    void getAccount() {
        // given
        user.registerAccount(BANK_NAME, ACCOUNT_NUMBER);
        userRepository.save(user);

        String url = "/users/account";

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
        assertAll(
                () -> assertThat(jsonPath.getString("data.name")).isEqualTo(NAME),
                () -> assertThat(jsonPath.getString("data.bankName")).isEqualTo(BANK_NAME),
                () -> assertThat(jsonPath.getString("data.accountNumber")).isEqualTo(ACCOUNT_NUMBER)
        );
    }

    @Test
    @DisplayName("계좌 삭제 검증")
    void removeAccount() {
        // given
        user.registerAccount(BANK_NAME, ACCOUNT_NUMBER);
        userRepository.save(user);

        String url = "/users/account";

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(url)
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(OK.value());
    }
}
