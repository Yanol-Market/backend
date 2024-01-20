package site.goldenticket.common.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import site.goldenticket.common.exception.CustomException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static site.goldenticket.common.response.ErrorCode.COMMON_JSON_PROCESSING_ERROR;
import static site.goldenticket.common.response.ErrorCode.COMMON_RESOURCE_NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_MESSAGE_DELIMITER = "," + System.lineSeparator();

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<CommonResponse<Void>> handleCustomException(CustomException e) {
        log.error("[CustomException] Message = {}", e.getMessage());
        return new ResponseEntity<>(
                CommonResponse.fail(e.getErrorCode().getMessage()),
                e.getErrorCode().getHttpStatus()
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<CommonResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("[HandleMethodArgumentNotValidException]", e);
        BindingResult bindingResult = e.getBindingResult();

        List<String> errorMessage = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.add("[" + fieldError.getField() + "] " + fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(CommonResponse.fail(String.join(
                ERROR_MESSAGE_DELIMITER,
                errorMessage)
        ));
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("[NoResourceFoundException] URL = {}, Message = {}", e.getResourcePath(), e.getMessage());
        return new ResponseEntity(CommonResponse.error(COMMON_RESOURCE_NOT_FOUND.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("[HttpMessageNotReadableException] Message = {}", e.getMessage());
        return ResponseEntity.badRequest().body(CommonResponse.error(COMMON_JSON_PROCESSING_ERROR.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception e) {
        log.error("[Exception] Message = {}", e.getMessage(), e);
        return new ResponseEntity<>(CommonResponse.error(), INTERNAL_SERVER_ERROR);
    }
}
