package com.investmetic.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    // 공통(Common) 성공 상태
    OK(HttpStatus.OK, "요청 성공하였습니다."),

    // 생성(Create) 성공 상태
    CREATED(HttpStatus.CREATED, "생성 성공하였습니다."),

    // 업데이트(Update) 성공 상태
    UPDATED(HttpStatus.OK, "업데이트 성공하였습니다."),

    // 삭제(Delete) 성공 상태
    DELETED(HttpStatus.OK, "삭제 성공하였습니다.");

    private final HttpStatus status; // HTTP 상태 코드
    private final String message; // 오류 메시지
}