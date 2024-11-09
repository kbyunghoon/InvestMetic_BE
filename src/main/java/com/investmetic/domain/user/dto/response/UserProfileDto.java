package com.investmetic.domain.user.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserProfileDto {
    private Long userId;
    private String username;
    private String email;
    private String imageUrL;
    private String nickname;
    private String phone;
    private Boolean infoAgreement;

}
