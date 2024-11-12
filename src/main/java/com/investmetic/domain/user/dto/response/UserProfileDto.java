package com.investmetic.domain.user.dto.response;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileDto {

    private Long userId;

    private String userName;

    private String email;

    private String imageUrl;

    private String nickname;

    private String phone;

    private Boolean infoAgreement;

}
