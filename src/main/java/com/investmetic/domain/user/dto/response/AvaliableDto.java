package com.investmetic.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 JSON에 포함되지 않음
public class AvaliableDto {
    private final Boolean isAvailable;
    private final String message;


    @Builder
    public AvaliableDto(Boolean isAvailable, String message) {
        this.isAvailable = isAvailable;
        this.message = message;
    }
}
