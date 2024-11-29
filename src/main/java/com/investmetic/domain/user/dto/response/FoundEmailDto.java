package com.investmetic.domain.user.dto.response;

import lombok.Getter;

@Getter
public class FoundEmailDto {
    private Boolean isFound;
    private String email;

    public FoundEmailDto (Boolean isFound, String email) {
        this.isFound = isFound;
        this.email = email;
    }
}

