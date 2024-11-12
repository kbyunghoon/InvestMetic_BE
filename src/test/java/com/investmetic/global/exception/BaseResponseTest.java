package com.investmetic.global.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class BaseResponseTest {

    @Test
    @DisplayName("데이터와 코드 포함한 성공 응답 생성 테스트")
    void successWithDataAndCode() {
        SuccessCode successCode = SuccessCode.OK;
        String data = "테스트";

        ResponseEntity<BaseResponse<String>> response = BaseResponse.success(successCode, data);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getIsSuccess());
        assertEquals(successCode.getMessage(), response.getBody().getMessage());
        assertEquals(data, response.getBody().getResult());
        assertNull(response.getBody().getCode());
    }

    @Test
    @DisplayName("데이터만 포함한 기본 성공 응답 생성 테스트")
    void successWithData() {
        String data = "테스트";

        ResponseEntity<BaseResponse<String>> response = BaseResponse.success(data);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getIsSuccess());
        assertEquals(SuccessCode.OK.getMessage(), response.getBody().getMessage());
        assertEquals(data, response.getBody().getResult());
        assertNull(response.getBody().getCode());
    }

    @Test
    @DisplayName("코드만 포함한 성공 응답 생성 테스트")
    void successWithCode() {
        SuccessCode successCode = SuccessCode.CREATED;

        ResponseEntity<BaseResponse<Void>> response = BaseResponse.success(successCode);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().getIsSuccess());
        assertEquals(successCode.getMessage(), response.getBody().getMessage());
        assertNull(response.getBody().getResult());
        assertNull(response.getBody().getCode());
    }

    @Test
    @DisplayName("기본 성공 응답 생성 테스트")
    void successDefault() {
        ResponseEntity<BaseResponse<Void>> response = BaseResponse.success();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getIsSuccess());
        assertEquals(SuccessCode.OK.getMessage(), response.getBody().getMessage());
        assertNull(response.getBody().getResult());
        assertNull(response.getBody().getCode());
    }

    @Test
    @DisplayName("에러 코드 포함한 실패 응답 생성 테스트")
    void failWithErrorCode() {
        ErrorCode errorCode = ErrorCode.USER_INFO_NOT_FOUND;

        BaseResponse<Void> response = BaseResponse.fail(errorCode);

        assertFalse(response.getIsSuccess());
        assertEquals(errorCode.getMessage(), response.getMessage());
        assertNull(response.getResult());
        assertEquals(errorCode.getCode(), response.getCode());
    }
}
