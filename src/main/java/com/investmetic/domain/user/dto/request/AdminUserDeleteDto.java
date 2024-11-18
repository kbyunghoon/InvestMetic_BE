package com.investmetic.domain.user.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUserDeleteDto {
    private String email;

    @Builder
    AdminUserDeleteDto(String email) {
        this.email = email;
    }
}
