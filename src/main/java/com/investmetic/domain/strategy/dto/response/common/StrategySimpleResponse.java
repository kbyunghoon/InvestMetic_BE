package com.investmetic.domain.strategy.dto.response.common;

import com.investmetic.domain.strategy.dto.ProfitRateChartDto;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Getter;

@Getter
public class StrategySimpleResponse {

    private Long strategyId;                        // 전략 ID
    private String strategyName;                    // 전략명
    private String traderImgUrl;                    // 트레이더 프로필 이미지
    private String nickname;                        // 트레이더 이름
    private List<String> stockTypeIconUrls;         // 종목 아이콘 이미지 경로 리스트
    private String tradeTypeIconUrl;                // 매매 유형 이미지 경로
    private ProfitRateChartDto profitRateChartData; // 수익률 그래프 데이터
    private long mdd;                               // MDD (최대자본인하금액)
    private double smScore;                         // smScore 수치
    private double cumulativeProfitRate;            // 누적 수익률
    private double recentYearProfitLossRate;        // 최근 1년 수익률
    private int subscriptionCount;                  // 구독 수
    private Boolean isSubscribed;                   // 구독 여부
    private double averageRating;                   // 평균 별점
    private int totalReviews;                       // 총리뷰수

    // querydsl 전용 생성자
    @QueryProjection
    public StrategySimpleResponse(Long strategyId, String strategyName, String traderImgUrl, String nickname,
                                  String tradeTypeIconUrl, long mdd, double smScore,
                                  double cumulativeProfitRate, double recentYearProfitLossRate, int subscriptionCount,
                                  double averageRating, int totalReviews) {
        this.strategyId = strategyId;
        this.strategyName = strategyName;
        this.traderImgUrl = traderImgUrl;
        this.nickname = nickname;
        this.tradeTypeIconUrl = tradeTypeIconUrl;
        this.mdd = mdd;
        this.smScore = smScore;
        this.cumulativeProfitRate = cumulativeProfitRate;
        this.recentYearProfitLossRate = recentYearProfitLossRate;
        this.subscriptionCount = subscriptionCount;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public void updateStockTypeIconUrls(List<String> stockTypeIconUrls) {
        this.stockTypeIconUrls = stockTypeIconUrls;
    }

    public void updateProfitRateChartData(ProfitRateChartDto profitRateChartData) {
        this.profitRateChartData = profitRateChartData;
    }

    public void updateIsSubscribed(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }
}