package com.investmetic.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserAdminPageRequestDto {

    //size는 일단 9명으로 고정하도록.
    private String keyword;

    private String condition;

    //ADMIN, TRADER, INVESTOR
    private String role;

    public UserAdminPageRequestDto(String keyword, String condition, String role) {
        this.keyword = keyword;
        this.condition = condition;
        this.role = role;
    }
}
