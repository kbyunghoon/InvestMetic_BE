package com.investmetic.global.util.stibee.dto;

import com.investmetic.domain.user.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteRequest {
    private String email;
    private Role role;
    public DeleteRequest(String email, Role role) {
        this.email = email;
        this.role = role;
    }
}
