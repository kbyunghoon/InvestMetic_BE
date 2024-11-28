package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
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
    private TradeTypeDto tradeType;
    private List<StockTypeDto> stockTypes;
    private MinimumInvestmentAmount minimumInvestmentAmount;
    private OperationCycle operationCycle;
    //    private String proposalFileUrl;
    private String description;

    @Builder
    public StrategyModifyInfoResponseDto(Strategy strategy, List<StockTypeDto> stockTypes, TradeTypeDto tradeType) {
        this.strategyName = strategy.getStrategyName();
        this.stockTypes = stockTypes;
        this.tradeType = tradeType;
        this.operationCycle = strategy.getOperationCycle();
        this.minimumInvestmentAmount = strategy.getMinimumInvestmentAmount();
//        this.proposalFileUrl = strategy.getProposalFilePath();
        this.description = strategy.getStrategyDescription();
    }
}
