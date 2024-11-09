package com.investmetic.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/**
 * BaseResponse는 API 요청에 대한 응답 표준화 클래스
 *
 * @param <T> 요청의 결과 데이터 타입
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private final Boolean isSuccess; // 상태 코드에 따른 Boolean
    private final String message;    // 에러 설명 또는 성공 메시지

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;          // 요청 성공 시 반환되는 결과 데이터

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer code;      // 성공 또는 오류 코드

    // 생성자
    private BaseResponse(Boolean isSuccess, String message, T result, Integer code) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.result = result;
        this.code = code;
    }

    // 성공 응답 생성 메서드
    public static <T> ResponseEntity<BaseResponse<T>> success(SuccessCode code, T data) {
        return ResponseEntity
                .status(code.getStatus())
                .body(new BaseResponse<>(true, code.getMessage(), data, null));
    }

    public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
        return ResponseEntity
                .status(SuccessCode.OK.getStatus())
                .body(new BaseResponse<>(true, SuccessCode.OK.getMessage(), data, null));
    }

    public static <T> ResponseEntity<BaseResponse<T>> success(SuccessCode code) {
        return ResponseEntity
                .status(code.getStatus())
                .body(new BaseResponse<>(true, code.getMessage(), null, null));
    }

    // 성공 응답 생성 메서드
    public static <T> ResponseEntity<BaseResponse<T>> success() {
        return ResponseEntity
                .status(SuccessCode.OK.getStatus())
                .body(new BaseResponse<>(true, SuccessCode.OK.getMessage(), null, null));
    }

    // 실패 응답 생성자
    public static <T> BaseResponse<T> fail(ErrorCode code) {
        return new BaseResponse<>(false, code.getMessage(), null, code.getStatusCode());
    }
}