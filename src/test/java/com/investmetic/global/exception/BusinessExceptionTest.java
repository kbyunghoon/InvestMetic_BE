package com.investmetic.global.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    @DisplayName("ErrorCode와 메시지를 포함한 예외 생성 테스트")
    void exceptionWithMessageAndCode() {

        String message = "Custom error message";
        ErrorCode errorCode = ErrorCode.USER_INFO_NOT_FOUND;

        BusinessException exception = new BusinessException(message, errorCode);

        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    @DisplayName("ErrorCode만 포함한 예외 생성 테스트")
    void exceptionWithCodeOnly() {
        ErrorCode errorCode = ErrorCode.USER_INFO_NOT_FOUND;

        BusinessException exception = new BusinessException(errorCode);

        assertEquals(errorCode.getMessage(), exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
    }
}
