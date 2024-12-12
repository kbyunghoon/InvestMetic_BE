package com.investmetic.domain.strategy.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.investmetic.domain.strategy.dto.StockTypeInfo;
import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class StrategyDetailResponse {

    private String strategyName;                    // 전략명
    private StockTypeInfo stockTypeInfo;            // 종목 이름, 아이콘 목록
    private String tradeTypeIconUrl;                // 매매 유형 이미지 경로
    private String tradeTypeName;                   // 매매유형 (자동, 반자동, 수동)
    private OperationCycle operationCycle;          // 투자 주기 (데이, 포지션)
    private String strategyDescription;             // 전략 상세 소개
    private double cumulativeProfitRate;            // 누적 수익률
    private double maxDrawdownRate;                 // 최대 자본 인하율(MDD)
    private double averageProfitLossRate;           // 평균 손익률
    private double profitFactor;                    // Profit Factor
    private double winRate;                         // 승률
    private int subscriptionCount;                  // 구독 수
    private Boolean isSubscribed;                   // 구독 여부
    private String traderImgUrl;                    // 트레이더 프로필 이미지
    private String nickname;                        // 트레이더 이름
    private String minimumInvestmentAmount;         // 최소 운용 금액
    private long initialInvestment;                 // 투자 원금
    private double kpRatio;                         // KP 비율
    private double smScore;                         // SM 점수
    private LocalDate finalProfitLossDate;          // 최종 손익 입력 일자
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;                    // 전략 등록일
    private Boolean hasProposal;


    @QueryProjection
    public StrategyDetailResponse(String strategyName, StockTypeInfo stockTypeInfo, String tradeTypeIconUrl,
                                  String tradeTypeName, OperationCycle operationCycle,
                                  String strategyDescription, double cumulativeProfitRate, double maxDrawdownRate,
                                  double averageProfitLossRate, double profitFactor, double winRate,
                                  int subscriptionCount, String traderImgUrl, String nickname,
                                  MinimumInvestmentAmount minimumInvestmentAmount, long initialInvestment,
                                  double kpRatio,
                                  double smScore, LocalDate finalProfitLossDate, LocalDateTime createdAt) {
        this.strategyName = strategyName;
        this.stockTypeInfo = stockTypeInfo;
        this.tradeTypeIconUrl = tradeTypeIconUrl;
        this.tradeTypeName = tradeTypeName;
        this.operationCycle = operationCycle;
        this.strategyDescription = strategyDescription;
        this.cumulativeProfitRate = cumulativeProfitRate;
        this.maxDrawdownRate = maxDrawdownRate;
        this.averageProfitLossRate = averageProfitLossRate;
        this.profitFactor = profitFactor;
        this.winRate = winRate;
        this.subscriptionCount = subscriptionCount;
        this.isSubscribed = false;
        this.traderImgUrl = traderImgUrl;
        this.nickname = nickname;
        this.minimumInvestmentAmount = minimumInvestmentAmount.getDescription();
        this.initialInvestment = initialInvestment;
        this.kpRatio = kpRatio;
        this.smScore = smScore;
        this.finalProfitLossDate = finalProfitLossDate;
        this.createdAt = createdAt;
    }

    public void updateIsSubscribed(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public void updateHasProposal(Boolean hasProposal) {
        this.hasProposal = hasProposal;
    }
}