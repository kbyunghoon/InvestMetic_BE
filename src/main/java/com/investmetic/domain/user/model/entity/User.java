package com.investmetic.domain.user.model.entity;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 회원 ID, 기본 키로 자동 증가됨

    private String username; // 사용자 이름 (로그인 아이디로 사용될 수 있음)

    private String nickname; // 사용자 닉네임 (표시 이름)

    private String email; // 이메일 주소

    private String password; // 비밀번호 (암호화 필요)

    @Column(length = 1000)
    private String imageUrL; // 프로필 이미지 URL

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

    // FIXME: 회원 임시용 생성자입니다. 충돌시 아래 생성코드는 삭제해주시고, 작성하신것으로 사용해주세요 -오정훈-
    @Builder
    public User(String username, String nickname, String email, String password, String imageUrL,
                String phone,
                String birthDate, String ipAddress, Boolean infoAgreement, LocalDate joinDate, LocalDate withdrawalDate,
                UserState userState, Boolean withdrawalStatus, Role role) {

        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.imageUrL = imageUrL;
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
}

