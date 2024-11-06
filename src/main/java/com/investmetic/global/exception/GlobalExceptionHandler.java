package com.investmetic.global.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
/**
 * 애플리케이션 전역 예외를 처리
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 커스텀 예외
     *
     * @param e 발생한 BaseException 인스턴스
     * @return HTTP 상태 코드와 함께 BaseResponse 형식의 오류 응답
     */
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<BaseResponse<Void>> handleBaseException(final BaseException e) {
        return ResponseEntity
                .status(e.getBaseResponseCode().getStatus().value())
                .body(new BaseResponse<>(e.getBaseResponseCode()));
    }
    /**
     * enum 타입이 일치하지 않을 때 발생하는 예외를 처리
     * @RequestParam으로 전달된 enum 타입의 값이 맞지 않을 때 주로 발생
     *
     * @param e MethodArgumentTypeMismatchException 인스턴스
     * @return HTTP 400 상태 코드와 함께 BaseResponse 형식의 오류 응답
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<BaseResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new BaseResponse<>(BaseResponseCode.INVALID_DATE));
    }
    /**
     * @Valid 또는 @Validated로 유효성 검사 실패 시 발생하는 예외를 처리
     * HttpMessageConverter에서 유효하지 않은 데이터를 변환할 때 발생
     * @RequestBody or @RequestPart 어노테이션 사용 시 발생
     *
     * @param e MethodArgumentNotValidException 인스턴스
     * @return HTTP 400 상태 코드와 함께 BaseResponse 형식의 오류 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<BaseResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(fieldError.getDefaultMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.name(), builder.toString()));
    }
    /**
     * 요청에 필수적인 Path Variable이 없을 때 발생하는 예외를 처리
     *
     * @param e HttpMessageNotReadableException 인스턴스
     * @return HTTP 400 상태 코드와 함께 BaseResponse 형식의 오류 응답
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new BaseResponse<>(BaseResponseCode.EMPTY_PATH_VARIABLE));
    }
    /**
     * 지원되지 않는 HTTP 메서드로 요청할 때 발생하는 예외를 처리
     *
     * @param e HttpRequestMethodNotSupportedException 인스턴스
     * @return HTTP 405 상태 코드와 함께 BaseResponse 형식의 오류 응답
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<BaseResponse<Void>> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new BaseResponse<>(BaseResponseCode.METHOD_NOT_ALLOWED));
    }
}