package com.investmetic.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 JSON에 포함되지 않음
public class AvaliableDto {
    private final Boolean isAvailable;
    private final String message;

    // 기존 생성자 유지
    public AvaliableDto(Boolean isAvailable) {
        this.isAvailable = isAvailable;
        this.message = null; // 메시지가 없는 경우
    }

    // 새로운 생성자 추가
    public AvaliableDto(Boolean isAvailable, String message) {
        this.isAvailable = isAvailable;
        this.message = message; // 메시지 추가
    }
}
