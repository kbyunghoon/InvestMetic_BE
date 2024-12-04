package com.investmetic.domain.user.dto.request;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    String email;
    String password;
    Boolean remember; // remember 필드 추가
}
