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
    private final Boolean isSuccess = false; // 상태 코드에 따른 Boolean
    private final String message; // 에러 설명

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result; // 요청 성공 시 반환되는 결과 데이터

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int code; // 오류 발생 시 반환되는 오류 코드

    /**
     * 실패 응답을 생성
     *
     * @param baseResponseCode 응답 코드 및 메시지를 포함한 BaseResponseCode 객체
     */
    public BaseResponse(BaseResponseCode baseResponseCode) {
        this.code = baseResponseCode.getCode();
        this.message = baseResponseCode.getMessage();
    }

    /**
     * 성공 응답을 생성
     *
     * @param message 성공 메시지
     * @param result 요청 성공 시 반환할 데이터
     */
    public BaseResponse(String message, T result) {
        this.message = message;
        this.result = result;
    }
}