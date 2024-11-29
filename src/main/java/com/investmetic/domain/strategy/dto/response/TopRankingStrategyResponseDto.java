package com.investmetic.domain.strategy.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Getter;

@Getter
public class TopRankingStrategyResponseDto {
    private Long strategyId;                        // 전략 ID
    private String strategyName;                    // 전략명
    private String traderImgUrl;                    // 트레이더 프로필 이미지
    private String nickname;                        // 트레이더 이름
    private List<Double> profitRateChartData; // 수익률 그래프 데이터
    private double smScore;                         // smScore 수치
    private double cumulativeProfitRate;            // 누적 수익률
    private int subscriptionCount;                  // 구독 수
    private double averageRating;                   // 평균 별점
    private int totalReviews;                       // 총리뷰수

    @QueryProjection
    public TopRankingStrategyResponseDto(Long strategyId, String strategyName, String traderImgUrl, String nickname,
                                            double smScore,
                                            double cumulativeProfitRate, int subscriptionCount, double averageRating,
                                            int totalReviews) {
        this.strategyId = strategyId;
        this.strategyName = strategyName;
        this.traderImgUrl = traderImgUrl;
        this.nickname = nickname;
        this.smScore = smScore;
        this.cumulativeProfitRate = cumulativeProfitRate;
        this.subscriptionCount = subscriptionCount;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public void updateProfitRateChartData(List<Double> profitRateChartData) {
        this.profitRateChartData = profitRateChartData;
    }
}
