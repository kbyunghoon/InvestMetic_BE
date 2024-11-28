package com.investmetic.domain.strategy.dto.response.common;

import com.investmetic.domain.strategy.dto.ProfitRateChartDto;
import com.investmetic.domain.strategy.dto.StockTypeInfo;
import lombok.Getter;

@Getter
public abstract class BaseStrategyResponse {
    private Long strategyId;                        // 전략 ID
    private String strategyName;                    // 전략명
    private String traderImgUrl;                    // 트레이더 프로필 이미지
    private String nickname;                        // 트레이더 이름
    private StockTypeInfo stockTypeInfo;
    private String tradeTypeIconUrl;                // 매매 유형 이미지 경로
    private String tradeTypeName;                   // 매매유형 이름 (자동, 반자동, 수동)
    private ProfitRateChartDto profitRateChartData; // 수익률 그래프 데이터
    private long mdd;                               // MDD (최대자본인하금액)
    private double smScore;                         // smScore 수치
    private double cumulativeProfitRate;            // 누적 수익률
    private double recentYearProfitLossRate;        // 최근 1년 수익률
    private int subscriptionCount;                  // 구독 수
    private double averageRating;                   // 평균 별점
    private int totalReviews;                       // 총리뷰수

    // 공통 생성자
    protected BaseStrategyResponse(Long strategyId, String strategyName, String traderImgUrl, String nickname,
                                String tradeTypeIconUrl, String tradeTypeName, long mdd, double smScore,
                                double cumulativeProfitRate, double recentYearProfitLossRate, int subscriptionCount,
                                double averageRating, int totalReviews) {
        this.strategyId = strategyId;
        this.strategyName = strategyName;
        this.traderImgUrl = traderImgUrl;
        this.nickname = nickname;
        this.tradeTypeIconUrl = tradeTypeIconUrl;
        this.tradeTypeName = tradeTypeName;
        this.mdd = mdd;
        this.smScore = smScore;
        this.cumulativeProfitRate = cumulativeProfitRate;
        this.recentYearProfitLossRate = recentYearProfitLossRate;
        this.subscriptionCount = subscriptionCount;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public void updateStockTypeInfo(StockTypeInfo stockTypeInfo) {
        this.stockTypeInfo = stockTypeInfo;
    }

    public void updateProfitRateChartData(ProfitRateChartDto profitRateChartData) {
        this.profitRateChartData = profitRateChartData;
    }

    // 템플릿 메서드
    public void updateIsSubscribed(boolean isSubscribed) {
    }
}
