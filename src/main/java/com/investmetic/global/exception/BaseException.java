package com.investmetic.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BaseException은 애플리케이션 전역에서 발생하는 커스텀 예외 정의 클래스
 * 특정 오류에서 예외를 처리하기 위해 사용
 */
@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    /**
     * 예외 상황을 설명하는 BaseResponseCode 인스턴스.
     * 예외의 구체적인 코드와 메시지 전달
     */
    private final ErrorCode errorCode;
}