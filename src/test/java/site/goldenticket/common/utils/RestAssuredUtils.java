package site.goldenticket.common.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public final class RestAssuredUtils {

    private RestAssuredUtils() {
    }

    public static ExtractableResponse<Response> restAssuredGet(String url) {
        return RestAssured
                .given().log().all()
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> restAssuredGetWithToken(
            String url,
            String accessToken
    ) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> restAssuredPost(
            String url,
            Object request
    ) {
        return RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> restAssuredPostWithToken(
            String url,
            Object request,
            String accessToken
    ) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .get(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> restAssuredPut(
            String url,
            Object request
    ) {
        return RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .put(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> restAssuredPutWithToken(
            String url,
            Object request,
            String accessToken
    ) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .put(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> restAssuredPatch(
            String url,
            Object request
    ) {
        return RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .patch(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> restAssuredPatchWithToken(
            String url,
            Object request,
            String accessToken
    ) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .patch(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> restAssuredDelete(String url) {
        return RestAssured
                .given().log().all()
                .when()
                .delete(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> restAssuredDeleteWithToken(
            String url,
            String accessToken
    ) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> restAssuredDeleteWithToken(
            String url,
            Object request,
            String accessToken
    ) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .delete(url)
                .then().log().all()
                .extract();
    }
}
