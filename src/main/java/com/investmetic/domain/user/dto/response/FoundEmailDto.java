package com.investmetic.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 JSON에 포함되지 않음
public class FoundEmailDto {
    private Boolean isFound;
    private String email;

    public FoundEmailDto(Boolean isFound, String email) {
        this.isFound = isFound;
        this.email = email;
    }
}

