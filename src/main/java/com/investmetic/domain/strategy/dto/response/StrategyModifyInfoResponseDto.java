package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StrategyModifyInfoResponseDto {
    private String strategyName;
    private Long tradeTypeId;
    private OperationCycle operationCycle;
    private List<Long> stockTypeIds;
    private MinimumInvestmentAmount minimumInvestmentAmount;
    private String proposalFileUrl;

    @Builder
    public StrategyModifyInfoResponseDto(Strategy strategy, List<StockType> stockTypes) {
        this.strategyName = strategy.getStrategyName();
        this.tradeTypeId = strategy.getTradeType().getTradeTypeId();
        this.operationCycle = strategy.getOperationCycle();
        this.stockTypeIds = stockTypes.stream()
                .map(StockType::getStockTypeId)
                .toList();
        this.minimumInvestmentAmount = strategy.getMinimumInvestmentAmount();
        this.proposalFileUrl = strategy.getProposalFilePath();
    }
}
