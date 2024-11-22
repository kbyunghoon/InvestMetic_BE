package com.investmetic.global.util.stibee.dto;

import com.investmetic.domain.user.model.Role;
import lombok.Getter;

@Getter
public class InfoUpdateRequest {
    private String email;
    private Boolean infoAgreement;
    private Role role;

}
