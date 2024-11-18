package com.investmetic.domain.user.dto.response;


import com.investmetic.domain.user.model.Role;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileDto {
    private Long userId;
    private String userName;
    private String email;
    private String imageUrl;
    private String nickname;
    private String phone;
    private Boolean infoAgreement;
    private Role role;

    @QueryProjection
    @Builder
    public UserProfileDto(Long userId, String userName, String email, String imageUrl, String nickname, String phone,
                          Boolean infoAgreement, Role role) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.phone = phone;
        this.infoAgreement = infoAgreement;
        this.role = role;
    }

}
