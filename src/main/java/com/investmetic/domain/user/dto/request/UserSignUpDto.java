package com.investmetic.domain.user.dto.request;

import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Builder
public class UserSignUpDto {
    private String username;
    private String nickname;
    private String phone;
    private String birthdate;
    private String password;
    private String email;
    private Role role;
    private String code;
    private Boolean infoAgreement; //정보제공 동의

    public static User toEntity(UserSignUpDto userSignUpDto, BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
                .userName(userSignUpDto.getUsername())
                .nickname(userSignUpDto.getNickname())
                .phone(userSignUpDto.getPhone())
                .birthDate(userSignUpDto.getBirthdate())
                .password(passwordEncoder.encode(userSignUpDto.getPassword())) // 비밀번호 암호화
                .email(userSignUpDto.getEmail())
                .role(userSignUpDto.getRole() != null ? userSignUpDto.getRole() : Role.INVESTOR) // 기본값 설정
                .infoAgreement(userSignUpDto.getInfoAgreement() != null
                        && userSignUpDto.getInfoAgreement()) // null 검사를 포함한 정보 제공 동의 설정
                .joinDate(LocalDate.now())
                .userState(UserState.ACTIVE) // 기본 사용자 상태 설정
                .build();
    }
}