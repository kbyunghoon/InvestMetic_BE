package com.investmetic.domain.user.model.entity;

import com.investmetic.domain.user.dto.request.UserModifyDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Builder(toBuilder = true)
@AllArgsConstructor
public class User extends BaseEntity {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserHistory> userHistory; //회원 변경 이력 (user Entity만 가지고 있음)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 회원 ID, 기본 키로 자동 증가됨

    @Column(name = "user_name")
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


    /**
     * update를 위한 메서드 Setter와 Builder 사용하지 않기위해 작성. 이메일은 변경하지 않는다.
     * <p>
     * DynamicUpdate를 사용해도 null이 들어가면 데이터를 null로 넣어줌.
     */
    public void updateUser(UserModifyDto userModifyDto, String imageUrl) {

        // 핸드폰 번호 수정.
        if (userModifyDto.getPhone() != null) {
            this.phone = userModifyDto.getPhone();
        }

        if (userModifyDto.getNickname() != null) {
            this.nickname = userModifyDto.getNickname();
        }

        // 기본 이미지를 이용하거나 새로운 사진을 업로드하는 경우. null 또는 presignedUrl
        if (Boolean.TRUE.equals(userModifyDto.getImageChange())) {
            this.imageUrl = imageUrl;
        }
    }

    // 해당 유저의 패스워드를 재설정한다.(회원 정보 페이지 비밀번호 수정, 로그인 페이지 비밀번호 재설정)
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public void addUserHistory(UserHistory userHistory) {
        if (this.userHistory == null) {
            this.userHistory = new ArrayList<>();
        }
        this.userHistory.add(userHistory);
    }

}

