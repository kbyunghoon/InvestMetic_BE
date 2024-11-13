package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StrategyDetailResponse {

    private String strategyName;                    // 전략명
    private List<String> stockTypeIconUrls;         // 종목 아이콘 이미지 경로 리스트
    private String tradeIconPath;                   // 매매 유형 이미지 경로
    private List<String> stockTypeNames;            // 투자 종목 이름 리스트
    private String tradeTypeName;                   // 투자 종류 (자동, 반자동, 수동)
    private OperationCycle investmentCycle;         // 투자 주기 (데이, 포지션)
    private String strategyDescription;             // 전략 상세 소개
    private double cumulativeProfitRate;            // 누적 수익률
    private double maxDrawdownRate;                 // 최대 자본 인하율
    private double averageProfitLossRate;           // 평균 손익률
    private double profitFactor;                    // Profit Factor
    private double winRate;                         // 승률
    private int subscriptionCount;                  // 구독 수
    private boolean isSubscribed;                   // 구독 여부
    private String imageUrl;                        // 트레이더 프로필 이미지
    private String nickname;                        // 트레이더 이름
    private MinimumInvestmentAmount minimumInvestmentAmount;         // 최소 운용 금액
    private long initialInvestment;                 // 투자 원금
    private double kpRatio;                         // KP 비율
    private double smScore;                         // SM 점수
    private LocalDate finalProfitLossDate;          // 최종 손익 입력 일자
    private LocalDate createdAt;                    // 전략 등록일


    @QueryProjection
    public StrategyDetailResponse(String strategyName, List<String> stockTypeIconUrls, String tradeIconPath,
                                  List<String> stockTypeNames, String tradeTypeName, OperationCycle investmentCycle,
                                  String strategyDescription, double cumulativeProfitRate, double maxDrawdownRate,
                                  double averageProfitLossRate, double profitFactor, double winRate,
                                  int subscriptionCount, String imageUrl, String nickname,
                                  MinimumInvestmentAmount minimumInvestmentAmount, long initialInvestment,
                                  double kpRatio,
                                  double smScore, LocalDate finalProfitLossDate, LocalDate createdAt) {
        this.strategyName = strategyName;
        this.stockTypeIconUrls = stockTypeIconUrls;
        this.tradeIconPath = tradeIconPath;
        this.stockTypeNames = stockTypeNames;
        this.tradeTypeName = tradeTypeName;
        this.investmentCycle = investmentCycle;
        this.strategyDescription = strategyDescription;
        this.cumulativeProfitRate = cumulativeProfitRate;
        this.maxDrawdownRate = maxDrawdownRate;
        this.averageProfitLossRate = averageProfitLossRate;
        this.profitFactor = profitFactor;
        this.winRate = winRate;
        this.subscriptionCount = subscriptionCount;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.minimumInvestmentAmount = minimumInvestmentAmount;
        this.initialInvestment = initialInvestment;
        this.kpRatio = kpRatio;
        this.smScore = smScore;
        this.finalProfitLossDate = finalProfitLossDate;
        this.createdAt = createdAt;
    }

    public void updateIsSubcried(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }
}