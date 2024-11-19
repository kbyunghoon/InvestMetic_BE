package com.investmetic.global.util.stibee.dto.request;

import lombok.Data;

@Data
public class EmailAndCode {

    // 이메일
    private String subscriber;

    //인증 코드
    private String code;

    public EmailAndCode(String email, String code) {
        this.subscriber = email;
        this.code = code;
    }
}
