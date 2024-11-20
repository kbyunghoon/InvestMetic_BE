package com.investmetic.domain.user.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordDto {
    private String password;
    private String email;

    @Builder
    public PasswordDto(String password, String email) {
        this.password = password;
        this.email = email;
    }
}
