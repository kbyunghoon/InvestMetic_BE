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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
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
    private IsPublic isPublic; // 공개여부

    @Enumerated(EnumType.STRING)
    private IsApproved isApproved; // 승인여부

    private Integer subscriptionCount; // 구독수

    @ColumnDefault("0.0")
    private Double averageRating = 0.0; // 평균별점

    public void updateAverageRating(Double newAverageRating) {
        this.averageRating = newAverageRating;
    }

    @PrePersist
    @PreUpdate
    private void setDefaultAverageRating() {
        if (this.averageRating == null) {
            this.averageRating = 0.0;
        }
    }

    // FIXME :  전략 임시용 생성자입니다. 충돌시 아래 생성코드는 삭제해주시고, 작성하신것으로 사용해주세요 -오정훈-
    @Builder
    public Strategy(Long strategyId, User user, TradeType tradeType, StrategyStatistics strategyStatistics,
                    String strategyName, OperationCycle operationCycle,
                    MinimumInvestmentAmount minimumInvestmentAmount, String strategyDescription,
                    String proposalFilePath,
                    IsPublic isPublic, IsApproved isApproved, Integer subscriptionCount, Double averageRating) {
        this.strategyId = strategyId;
        this.user = user;
        this.tradeType = tradeType;
        this.strategyStatistics = strategyStatistics;
        this.strategyName = strategyName;
        this.operationCycle = operationCycle;
        this.minimumInvestmentAmount = minimumInvestmentAmount;
        this.strategyDescription = strategyDescription;
        this.proposalFilePath = proposalFilePath;
        this.isPublic = isPublic;
        this.isApproved = isApproved;
        this.subscriptionCount = subscriptionCount;
        this.averageRating = averageRating;
    }
}