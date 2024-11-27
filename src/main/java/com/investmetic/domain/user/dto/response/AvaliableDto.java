package com.investmetic.domain.user.dto.response;

import lombok.Getter;

@Getter
public class AvaliableDto {
    private Boolean isAvailable;
    // 생성자
    public AvaliableDto(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
