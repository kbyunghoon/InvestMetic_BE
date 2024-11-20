package com.investmetic.domain.user.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatistic extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userStatisticId;
    private Integer newUserCount; // 신규 가입자 수
    private Integer newWithdrawalCount; // 신규 탈퇴자 수
    private Integer totalUserCount; // 누적 가입자 수
    private Integer totalWithdrawalCount; // 누적 탈퇴자 수
}
