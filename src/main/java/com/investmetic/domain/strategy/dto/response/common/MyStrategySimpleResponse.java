package com.investmetic.domain.strategy.dto.response.common;

import com.investmetic.domain.strategy.model.IsPublic;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class MyStrategySimpleResponse extends BaseStrategyResponse {

    private IsPublic isPublic;

    @QueryProjection
    public MyStrategySimpleResponse(Long strategyId, String strategyName, String traderImgUrl, String nickname,
                                    String tradeTypeIconUrl, String tradeTypeName, long mdd, double smScore,
                                    double cumulativeProfitRate, double recentYearProfitLossRate, int subscriptionCount,
                                    double averageRating, int totalReviews, IsPublic isPublic) {
        super(strategyId, strategyName, traderImgUrl, nickname, tradeTypeIconUrl, tradeTypeName, mdd,
                smScore, cumulativeProfitRate, recentYearProfitLossRate, subscriptionCount, averageRating,
                totalReviews);
        this.isPublic = isPublic;
    }
}