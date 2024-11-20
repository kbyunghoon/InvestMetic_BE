package com.investmetic.domain.user.dto.request;

import com.investmetic.domain.user.dto.object.RoleCondition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleUpdateRequestDto {
    private RoleCondition newRole;

    @Builder
    RoleUpdateRequestDto(RoleCondition newRole) {
        this.newRole = newRole;
    }
}
