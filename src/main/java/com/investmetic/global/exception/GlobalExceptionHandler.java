package com.investmetic.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 애플리케이션 전역 예외를 처리하는 클래스
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     *
     * @param e 발생한 BusinessException 인스턴스
     * @return HTTP 상태 코드와 함께 BaseResponse 형식의 오류 응답
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<BaseResponse<Void>> handleBusinessException(final BusinessException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(BaseResponse.fail(e.getErrorCode()));
    }

    /**
     * 일반적인 모든 예외 처리 (Exception)
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<BaseResponse<Void>> handleGeneralException(final Exception e) {
        log.error("Unhandled Exception 발생: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    /**
     * enum 타입이 일치하지 않을 때 발생하는 예외 처리
     *
     * @RequestParam 으로 전달된 enum 타입의 값이 맞지 않을 때 주로 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<BaseResponse<String>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException 예외 처리: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(ErrorCode.INVALID_DATE));
    }

    /**
     * @Valid 또는 @Validated로 유효성 검사 실패 시 발생하는 예외 처리
     * @RequestBody 또는 @RequestPart 사용 시 발생
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    protected ResponseEntity<BaseResponse<String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException 예외 처리 : {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(ErrorCode.INVALID_INPUT_VALUE));
    }

    /**
     * 요청에 필수적인 Path Variable이 없을 때 발생하는 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException 예외 처리 : {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(ErrorCode.EMPTY_PATH_VARIABLE));
    }

    /**
     * 지원되지 않는 HTTP 메서드로 요청할 때 발생하는 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<BaseResponse<Void>> handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException 예외 처리 : {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(BaseResponse.fail(ErrorCode.METHOD_NOT_ALLOWED));
    }

    /**
     * 시큐리티 인가 예외 (권한없음)
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity<BaseResponse<Void>> authorizationDeniedException(
            final AuthorizationDeniedException e) {
        log.error("AuthorizationDeniedException 예외 처리 : {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.fail(ErrorCode.AUTHORIZATION_DENIED));
    }


}