package com.investmetic.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    OK(HttpStatus.OK, "요청이 성공했습니다."),
    CREATED(HttpStatus.CREATED, "자원이 성공적으로 생성되었습니다."),
    UPDATED(HttpStatus.OK, "자원이 성공적으로 업데이트되었습니다."),
    DELETED(HttpStatus.OK, "자원이 성공적으로 삭제되었습니다.");

    private final HttpStatus status; // HTTP 상태 코드
    private final String message; // 오류 메시지

    public int getStatusCode() {
        return status.value();
    }
}