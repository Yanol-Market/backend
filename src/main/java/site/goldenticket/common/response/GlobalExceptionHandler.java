package site.goldenticket.common.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.goldenticket.common.exception.CustomException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<CommonResponse<Void>> handleCustomException(CustomException e) {
        log.error("[CustomException] Message = {}", e.getMessage());
        return new ResponseEntity<>(
                CommonResponse.fail(e.getErrorCode().getMessage()),
                e.getErrorCode().getHttpStatus()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("[HandleMethodArgumentNotValidException]", e);
        BindingResult bindingResult = e.getBindingResult();

        List<String> errorMessage = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.add("[" + fieldError.getField() + "] " + fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(CommonResponse.fail(String.join(
                "," + System.lineSeparator(),
                errorMessage)
        ));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception e) {
        log.error("[Exception] Message = {}", e.getMessage(), e);
        return new ResponseEntity<>(CommonResponse.error(), INTERNAL_SERVER_ERROR);
    }
}
