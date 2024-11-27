package com.investmetic.domain.user.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 유저 약관 확인.
 * */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTerms {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name ="term_version_id",nullable = false)
    private String termVersionId ="1"; // 현재는 1로 해놓기.

    private LocalDateTime agreedDate;

    private Boolean agreed;
}
