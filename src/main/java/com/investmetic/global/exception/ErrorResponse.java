package com.investmetic.global.exception;


import com.investmetic.global.exception.error.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private Boolean isSuccess = false; // 응답 성공 여부 (오류인 경우 false)
    private String message; // 오류 메시지
    private int code; // 오류 코드

    // ErrorCode를 통해 ErrorResponse를 생성하는 생성자
    private ErrorResponse(final ErrorCode errorCode) {
        this.isSuccess = false;
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
    }

    // ErrorCode를 기반으로 ErrorResponse 인스턴스 생성
    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    // MethodArgumentTypeMismatchException을 처리하는 정적 팩토리 메서드
    public static ErrorResponse of(MethodArgumentTypeMismatchException e) {
        return new ErrorResponse(ErrorCode.INVALID_TYPE_VALUE);
    }
}