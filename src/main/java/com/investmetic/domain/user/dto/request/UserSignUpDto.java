package com.investmetic.domain.user.dto.request;

import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Builder
public class UserSignUpDto {
    @NotNull
    private String username;

    @NotNull
    private String nickname;

    @NotNull
//    @Pattern(regexp = "^010(\\d{3}|\\d{4})(\\d{4})$") // 010 필수
    private String phone;

    @NotNull
    @Pattern(regexp = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")
    private String birthdate;

    @NotNull
    private String password;

    @NotNull
    private String email;

    private Role role;

    @NotNull
    private String code;

    @NotNull
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