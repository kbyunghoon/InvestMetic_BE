package com.investmetic.domain.strategy.dto;

import com.investmetic.domain.strategy.model.MinimumInvestmentAmountEnum;
import com.investmetic.domain.strategy.model.TradingStrategyType;
import java.util.List;
import lombok.Getter;

@Getter
public class StrategyRegisterRequestDto {
    private String strategyName; // 전략명
    private Long tradeTypeId; // 매매유형 ID
    private TradingStrategyType tradingStrategyType; // 운용주기
    private List<Long> stockTypeIds; // 종목 유형 ID 리스트
    private MinimumInvestmentAmountEnum minimumInvestmentAmount; // 최소운용금액
    private ProposalFileDto proposalFile; // 제안서 파일 정보

    @Getter
    public static class ProposalFileDto {
        private String proposalFileName; // 파일명
        private int proposalFileSize; // 파일 크기
    }
}
