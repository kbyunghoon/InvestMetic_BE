package com.investmetic.domain.user.model.entity;

import com.investmetic.domain.user.dto.request.UserModifyDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 회원 ID, 기본 키로 자동 증가됨

    private String userName; // 사용자 이름 (로그인 아이디로 사용될 수 있음)

    private String nickname; // 사용자 닉네임 (표시 이름)

    private String email; // 이메일 주소

    private String password; // 비밀번호 (암호화 필요)

    @Column(length = 1000)
    private String imageUrl; // 프로필 이미지 URL

    private String phone; // 전화번호

    private String birthDate; // 생년월일 (YYYYMMDD 형식)

    private String ipAddress; // 마지막 로그인 시 사용한 IP 주소

    private Boolean infoAgreement; // 정보 제공 동의 여부 (true: 동의, false: 비동의)

    private LocalDate joinDate; // 가입일자

    private LocalDate withdrawalDate; // 탈퇴일자 (탈퇴한 경우에만 값이 있음)

    @Enumerated(EnumType.STRING)
    private UserState userState; // 회원 상태

    private Boolean withdrawalStatus; // 탈퇴 여부

    @Enumerated(EnumType.STRING)
    private Role role; // 회원 등급 또는 역할

    @Builder
    public User(String userName, String nickname, String email, String password, String imageUrl,
                String phone,
                String birthDate, String ipAddress, Boolean infoAgreement, LocalDate joinDate, LocalDate withdrawalDate,
                UserState userState, Boolean withdrawalStatus, Role role) {

        this.userName = userName;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.phone = phone;
        this.birthDate = birthDate;
        this.ipAddress = ipAddress;
        this.infoAgreement = infoAgreement;
        this.joinDate = joinDate;
        this.withdrawalDate = withdrawalDate;
        this.userState = userState;
        this.withdrawalStatus = withdrawalStatus;
        this.role = role;
    }


    /**
     * update를 위한 메서드 Setter와 Builder 사용하지 않기위해 작성. 이메일은 변경하지 않는다.
     *
     * DynamicUpdate를 사용해도 null이 들어가면 데이터를 null로 넣어줌.
     */
    public void updateUser(UserModifyDto userModifyDto, String imageUrl) {

        // 필드가 null이 아닌 경우에만 업데이트
        if (userModifyDto.getPassword() != null) {
            // 개인정보 수정 페이지 들어가기 전에 패스워드 인증하고 들어가므로 바로 바꿀 수 있게 한다.
            this.password = userModifyDto.getPassword();
        }

        if (userModifyDto.getPhone() != null) {
            this.phone = userModifyDto.getPhone();
        }

        if (userModifyDto.getNickname() != null) {
            this.nickname = userModifyDto.getNickname();
        }

        if (userModifyDto.getInfoAgreement() != null) {
            this.infoAgreement = userModifyDto.getInfoAgreement();
        }

        // null 가능.
        this.imageUrl = imageUrl;
    }

}

