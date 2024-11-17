package com.investmetic.domain.user.dto.request;

import com.investmetic.domain.user.dto.object.ColumnCondition;
import com.investmetic.domain.user.dto.object.RoleCondition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAdminPageRequestDto {

    //size는 일단 9명으로 고정하도록.
    private String keyword;

    private ColumnCondition condition;

    //ADMIN, TRADER, INVESTOR, ALL
    private RoleCondition role;

    private UserAdminPageRequestDto(String keyword, ColumnCondition condition, RoleCondition role) {
        this.keyword = keyword;
        this.condition = condition;
        this.role = role;
    }

    public static UserAdminPageRequestDto createDto(String keyword, ColumnCondition condition, RoleCondition role){
        return new UserAdminPageRequestDto(keyword, condition, role);
    }
}
