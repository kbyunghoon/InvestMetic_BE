package com.investmetic.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * BaseResponse는 API 요청에 대한 응답 표준화 클래스
 *
 * @param <T> 요청의 결과 데이터 타입
 */
@Getter
public class BaseResponse<T> {

    private final Boolean isSuccess; // 상태 코드에 따른 Boolean
    private final String message;    // 에러 설명 또는 성공 메시지

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;          // 요청 성공 시 반환되는 결과 데이터

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer code;      // 성공 또는 오류 코드

    // 생성자
    private BaseResponse(Boolean isSuccess, Integer code, String message, T result) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.result = result;
    }

    // 성공 응답 생성자
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(true, null, SuccessCode.OK.getMessage(), null);
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, null, SuccessCode.OK.getMessage(), data);
    }

    public static <T> BaseResponse<T> success(SuccessCode code, T data) {
        return new BaseResponse<>(true, null, code.getMessage(), data); // 성공 시 code는 null로 설정
    }

    // 실패 응답 생성자
    public static <T> BaseResponse<T> fail(ErrorCode code) {
        return new BaseResponse<>(false, code.getStatusCode(), code.getMessage(), null);
    }
}