package site.goldenticket.domain.user.controller;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import site.goldenticket.common.config.ApiTest;
import site.goldenticket.domain.user.dto.*;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static site.goldenticket.common.utils.RestAssuredUtils.*;
import static site.goldenticket.common.utils.UserUtils.*;

@DisplayName("UserController 검증")
class UserControllerTest extends ApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 검증")
    void join() {
        // given
        JoinRequest request = createJoinRequest();
        String url = "/users";

        // when
        ExtractableResponse<Response> result = restAssuredPost(url, request);

        // then
        assertThat(result.statusCode()).isEqualTo(CREATED.value());
    }

    @Test
    @DisplayName("사용자 정보 조회 검증")
    void getUserInfo() {
        // given
        String url = "/users/me";

        // when
        ExtractableResponse<Response> result = restAssuredGetWithToken(url, accessToken);

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
    @DisplayName("사용자 삭제 검증")
    void removeUser() {
        // given
        RemoveUserRequest request = createRemoveUserRequest();
        String url = "/users";

        // when
        ExtractableResponse<Response> result = restAssuredDeleteWithToken(url, request, accessToken);

        // then
        assertThat(result.statusCode()).isEqualTo(OK.value());
        assertThat(userRepository.findById(user.getId()).isPresent()).isFalse();
    }

    @Test
    @DisplayName("사용자 프로필 수정 검증")
    void changeProfile() {
        // given
        String changeNickname = "changeNickname";
        ChangeProfileRequest request = createChangeProfileRequest(changeNickname);
        String url = "/users/me";

        // when
        ExtractableResponse<Response> result = restAssuredPutWithToken(url, request, accessToken);

        // then
        assertThat(result.statusCode()).isEqualTo(OK.value());

        User findUser = userRepository.findById(user.getId()).orElseThrow();
        assertAll(
                () -> assertThat(findUser.getId()).isEqualTo(user.getId()),
                () -> assertThat(findUser.getEmail()).isEqualTo(EMAIL),
                () -> assertThat(findUser.getName()).isEqualTo(NAME),
                () -> assertThat(findUser.getNickname()).isEqualTo(changeNickname),
                () -> assertThat(findUser.getImageUrl()).isNull(),
                () -> assertThat(findUser.getPhoneNumber()).isEqualTo(PHONE_NUMBER),
                () -> assertThat(findUser.getYanoljaId()).isEqualTo(YANOLJA_ID)
        );
    }

    @Test
    @DisplayName("비밀번호 변경 검증")
    void changePassword() {
        // given
        ChangePasswordRequest request = createChangePasswordRequest();
        String url = "/users/password";

        // when
        ExtractableResponse<Response> result = restAssuredPatchWithToken(url, request, accessToken);

        // then
        assertThat(result.statusCode()).isEqualTo(OK.value());

        User findUser = userRepository.findById(this.user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(CHANGE_PASSWORD, findUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("계좌 등록 검증")
    void registerAccount() {
        // given
        RegisterAccountRequest request = createRegisterAccountRequest();
        String url = "/users/account";

        // when
        ExtractableResponse<Response> result = restAssuredPatchWithToken(url, request, accessToken);

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
        ExtractableResponse<Response> result = restAssuredGetWithToken(url, accessToken);

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
        ExtractableResponse<Response> result = restAssuredDeleteWithToken(url, accessToken);

        // then
        assertThat(result.statusCode()).isEqualTo(OK.value());
    }
}
