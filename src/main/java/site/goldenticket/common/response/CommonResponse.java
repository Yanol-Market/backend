package site.goldenticket.common.response;

import lombok.Getter;

import static site.goldenticket.common.response.ErrorCode.COMMON_SYSTEM_ERROR;
import static site.goldenticket.common.response.Status.*;

@Getter
public class CommonResponse<T> {

    private final Status status;
    private final String message;
    private final T data;

    private CommonResponse(Status status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static CommonResponse<Void> ok() {
        return ok(null);
    }

    public static <T> CommonResponse<T> ok(T data) {
        return ok(null, data);
    }

    public static <T> CommonResponse<T> ok(String message, T data) {
        return new CommonResponse<>(SUCCESS, message, data);
    }

    public static <T> CommonResponse<T> fail(T data) {
        return fail(null, data);
    }

    public static CommonResponse<Void> fail(String message) {
        return fail(message, null);
    }

    public static <T> CommonResponse<T> fail(String message, T data) {
        return new CommonResponse<>(FAIL, message, data);
    }

    public static CommonResponse<Void> error() {
        return error(COMMON_SYSTEM_ERROR.getMessage());
    }

    public static CommonResponse<Void> error(String message) {
        return new CommonResponse<>(ERROR, message, null);
    }
}
