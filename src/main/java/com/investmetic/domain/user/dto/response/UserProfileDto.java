package com.investmetic.domain.user.dto.response;


import com.investmetic.domain.user.model.Role;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserProfileDto {

    private Long userId;

    private String userName;

    private String email;

    private String imageUrl;

    private String nickname;

    private String phone;

    private Boolean infoAgreement;

    private Role role;

}
