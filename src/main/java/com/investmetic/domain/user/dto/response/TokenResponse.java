package com.investmetic.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenResponse {
    private String accessToken;
    private String message;

    public TokenResponse(String accessToken, String message) {
        this.accessToken = accessToken;
        this.message = message;
    }
}
