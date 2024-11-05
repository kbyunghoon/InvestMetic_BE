package com.investmetic.domain.accountVerification.model.entity;


import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class AccountVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountVerificationId; // 계좌 인증 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy; // 전략 ID와의 관계

    private String title; // 인증 제목

    @Column(length = 1000)
    private String accountVerificationUrl; // 계좌 인증 이미지 주소

}