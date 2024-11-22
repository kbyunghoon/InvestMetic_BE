package com.investmetic.domain.strategy.model.entity;

import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Strategy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long strategyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_type_id", nullable = false)
    private TradeType tradeType; // 매매유형

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategyStatistics_id")
    private StrategyStatistics strategyStatistics;

    private String strategyName; // 전략명

    @Enumerated(EnumType.STRING)
    private OperationCycle operationCycle; // 운용주기

    @Enumerated(EnumType.STRING)
    private MinimumInvestmentAmount minimumInvestmentAmount; // 최소운용가능금액

    @Column(length = 3000)
    private String strategyDescription; // 전략소개

    @Column(length = 1000)
    private String proposalFilePath; // 제안서 파일경로

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IsPublic isPublic = IsPublic.PRIVATE; // 공개여부 (default : 비공개)

    @Enumerated(EnumType.STRING)
    private IsApproved isApproved; // 승인여부

    @ColumnDefault("0")
    @Builder.Default
    private Integer subscriptionCount = 0; // 구독수

    @ColumnDefault("0.0")
    @Builder.Default
    private Double averageRating = 0.0; // 평균별점

    @ColumnDefault("0")
    @Builder.Default
    private Integer reviewCount = 0; // 리뷰수

    public void updateAverageRating(Double newAverageRating) {
        this.averageRating = newAverageRating;
    }

    public void incrementReviewCount() {
        reviewCount++;
    }

    public void decrementReviewCount() {
        reviewCount--;
    }

    @PrePersist
    @PreUpdate
    private void setDefaultAverageRating() {
        if (this.averageRating == null) {
            this.averageRating = 0.0;
        }
    }

    public void setIsPublic(IsPublic isPublic) {
        this.isPublic = isPublic;
    }

    public void plusSubscriptionCount() {
        this.subscriptionCount+=1;
    }
    public void minusSubscriptionCount() {
        this.subscriptionCount-=1;
    }
}