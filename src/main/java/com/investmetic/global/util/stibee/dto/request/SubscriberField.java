package com.investmetic.global.util.stibee.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)// Null 값인 필드 제외
public class SubscriberField {

    private String email; // 이메일

    private final String name; // 이름

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime termDate; // 약관 동의일

    @JsonProperty("$ad_agreed")
    private final String adAgreed ="Y";


    private SubscriberField(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public static SubscriberField create(String email, String name){

        return new SubscriberField(email, name);
    }

    //약관 날짜 설정.
    public void updateTermDate(){
        this.termDate = LocalDateTime.now();
    }

    public void setEmail(String email){
        this.email = email;
    }

}
