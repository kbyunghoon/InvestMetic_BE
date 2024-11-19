package com.investmetic.global.util.stibee.dto.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EmailAndName {
    private final String email;
    private final String name;

    @JsonProperty("$ad_agreed")
    private final String ad_agreed;


    private EmailAndName(String email, String name, String adAgreed) {
        this.email = email;
        this.name = name;
        ad_agreed = adAgreed;
    }

    public static EmailAndName create(String email, String name, String adAgreed){
        return new EmailAndName(email, name, adAgreed);
    }
}
