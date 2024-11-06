package com.investmetic.global.exception;

<<<<<<< Updated upstream
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
=======
import com.investmetic.global.exception.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * javax.validation.Valid or @Validated 바인딩 오류 시 발생.
     * 주로 @RequestBody, @RequestPart에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * enum 타입 일치하지 않아 바인딩 못할 경우 발생.
     * 주로 @RequestParam enum으로 바인딩하지 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
        log.error("handleBusinessException", e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * 존재하지 않는 URL에 대한 요청 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("handleNoHandlerFoundException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.ENTITY_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * HTTP 메서드가 지원되지 않는 경우 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupported", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 기타 모든 예외에 대한 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
>>>>>>> Stashed changes
    }
}