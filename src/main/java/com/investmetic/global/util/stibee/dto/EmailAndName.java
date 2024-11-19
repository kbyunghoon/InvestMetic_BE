package com.investmetic.global.util.stibee.dto;

import lombok.Getter;

@Getter
public class EmailAndName {
    private final String email;
    private final String name;


    private EmailAndName(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public static EmailAndName create(String email, String name){
        return new EmailAndName(email, name);
    }
}
