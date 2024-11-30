package com.investmetic.domain.user.dto.request;

import com.investmetic.domain.user.dto.object.ImageMetadata;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
     회원 정보 수정 시 사용할 Dto
    data : password, imageUrl, phone, email, nickName, infoAgreement, infoReceive
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserModifyDto {

    private String nickname;

    private String password;

    // imageDto의 정보로 Service에서 유효성 검사 진행 후 presigned url 요청, 기존의 s3에 저장된 사진 제거.
    @Valid
    private ImageMetadata imageDto;

    private String phone;

    //디자인을 보면 email은 변경하지 못하게 한다. 토큰의 email과 dto의 email이 일치하는지 검증.
    private String email;

    @NotNull
    private Boolean imageChange;

    @Builder
    public UserModifyDto(String nickname, String password, ImageMetadata imageDto, String phone, String email,
                         Boolean imageChange) {
        this.nickname = nickname;
        this.password = password;
        this.imageDto = imageDto;
        this.phone = phone;
        this.email = email;
        this.imageChange = imageChange;
    }

}
