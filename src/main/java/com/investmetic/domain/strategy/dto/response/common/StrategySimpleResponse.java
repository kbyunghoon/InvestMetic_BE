package com.investmetic.domain.strategy.dto.response.common;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class StrategySimpleResponse extends BaseStrategyResponse {

    private Boolean isSubscribed; // 구독 여부

    @QueryProjection
    public StrategySimpleResponse(Long strategyId, String strategyName, String traderImgUrl, String nickname,
                                  String tradeTypeIconUrl, long mdd, double smScore,
                                  double cumulativeProfitRate, double recentYearProfitLossRate, int subscriptionCount,
                                  double averageRating, int totalReviews) {
        super(strategyId, strategyName, traderImgUrl, nickname, tradeTypeIconUrl, mdd, smScore, cumulativeProfitRate,
                recentYearProfitLossRate, subscriptionCount, averageRating, totalReviews);
    }

    @Override
    public void updateIsSubscribed(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }
}