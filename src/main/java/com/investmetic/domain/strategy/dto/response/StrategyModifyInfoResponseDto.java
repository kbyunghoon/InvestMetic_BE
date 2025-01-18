package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.model.IsPublic;
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
    private String proposalFileUrl;
    private String proposalFileName;
    private IsPublic isPublic;
    private String description;

    @Builder
    public StrategyModifyInfoResponseDto(Strategy strategy, List<StockTypeDto> stockTypes, TradeTypeDto tradeType) {
        this.strategyName = strategy.getStrategyName();
        this.stockTypes = stockTypes;
        this.tradeType = tradeType;
        this.operationCycle = strategy.getOperationCycle();
        this.minimumInvestmentAmount = strategy.getMinimumInvestmentAmount();
        this.isPublic = strategy.getIsPublic();
        this.description = strategy.getStrategyDescription();

        if (strategy.getProposalFilePath() != null) {
            this.proposalFileUrl = "/" + strategy.getStrategyId() + "/download-proposal";
            this.proposalFileName = "/" + strategy.getStrategyId() + "/download-proposal";
        } else {
            this.proposalFileUrl = null;
            this.proposalFileName = extractFileName(strategy.getProposalFilePath());
        }
    }
    private String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        return fileName.length() > 8 ? fileName.substring(8) : fileName;
    }
}
