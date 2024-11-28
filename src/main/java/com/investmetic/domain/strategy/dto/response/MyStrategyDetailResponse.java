package com.investmetic.domain.strategy.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.investmetic.domain.strategy.dto.StockTypeInfo;
import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MyStrategyDetailResponse {

    private String strategyName;                    // 전략명
    private StockTypeInfo stockTypeInfo;            // 종목 이름, 아이콘 목록
    private String tradeTypeIconURL;                // 매매 유형 이미지 경로
    private String tradeTypeName;                   // 투자 종류 (자동, 반자동, 수동)
    private OperationCycle operationCycle;          // 투자 주기 (데이, 포지션)
    private String strategyDescription;             // 전략 상세 소개
    private int subscriptionCount;                  // 구독 수
    private String traderImgUrl;                    // 트레이더 프로필 이미지
    private String nickname;                        // 트레이더 이름
    private String minimumInvestmentAmount;         // 최소 운용 금액
    private long initialInvestment;                 // 투자 원금
    private double kpRatio;                         // KP 비율
    private double smScore;                         // SM 점수
    private LocalDate finalProfitLossDate;          // 최종 손익 입력 일자
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;                    // 전략 등록일
    private IsPublic isPublic;                      // 공개여부
    private IsApproved isApproved;                  // 승인여부


    @QueryProjection
    public MyStrategyDetailResponse(String strategyName, String tradeTypeIconURL, StockTypeInfo stockTypeInfo,
                                    String tradeTypeName, OperationCycle operationCycle,
                                    String strategyDescription, int subscriptionCount, String traderImgUrl,
                                    String nickname, MinimumInvestmentAmount minimumInvestmentAmount,
                                    long initialInvestment, double kpRatio, double smScore,
                                    LocalDate finalProfitLossDate, LocalDateTime createdAt, IsPublic isPublic,
                                    IsApproved isApproved) {
        this.strategyName = strategyName;
        this.stockTypeInfo = stockTypeInfo;
        this.tradeTypeIconURL = tradeTypeIconURL;
        this.tradeTypeName = tradeTypeName;
        this.operationCycle = operationCycle;
        this.strategyDescription = strategyDescription;
        this.subscriptionCount = subscriptionCount;
        this.traderImgUrl = traderImgUrl;
        this.nickname = nickname;
        this.minimumInvestmentAmount = minimumInvestmentAmount.getDescription();
        this.initialInvestment = initialInvestment;
        this.kpRatio = kpRatio;
        this.smScore = smScore;
        this.finalProfitLossDate = finalProfitLossDate;
        this.createdAt = createdAt;
        this.isPublic = isPublic;
        this.isApproved = isApproved;
    }

}