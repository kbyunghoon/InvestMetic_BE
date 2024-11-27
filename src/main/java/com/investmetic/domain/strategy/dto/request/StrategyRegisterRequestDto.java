package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.global.dto.ProposalFileDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StrategyRegisterRequestDto {
    private String strategyName; // 전략명
    private Long tradeTypeId; // 매매유형 ID
    private OperationCycle operationCycle; // 운용주기
    private List<Long> stockTypeIds; // 종목 유형 ID 리스트
    private MinimumInvestmentAmount minimumInvestmentAmount; // 최소운용금액
    private ProposalFileDto proposalFile; // 제안서 파일 정보
    private String description;

    @Builder
    public StrategyRegisterRequestDto(String strategyName, Long tradeTypeId, OperationCycle operationCycle,
                                      List<Long> stockTypeIds, MinimumInvestmentAmount minimumInvestmentAmount,
                                      ProposalFileDto proposalFile, String description) {
        this.strategyName = strategyName;
        this.tradeTypeId = tradeTypeId;
        this.operationCycle = operationCycle;
        this.stockTypeIds = stockTypeIds;
        this.minimumInvestmentAmount = minimumInvestmentAmount;
        this.proposalFile = proposalFile;
        this.description = description;
    }
}
