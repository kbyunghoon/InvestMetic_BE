package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.global.dto.ProposalFileDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StrategyRegisterRequestDto {
    @NotNull(message = "전략명을 입력해주세요.")
    @NotBlank(message = "전략명은 비어 있을 수 없습니다.")
    private String strategyName;

    @NotNull(message = "매매 유형을 선택해주세요.")
    private Long tradeTypeId;

    @NotNull(message = "운용 주기를 입력해주세요.")
    private OperationCycle operationCycle;

    @NotEmpty(message = "종목 유형을 선택해주세요.")
    private List<Long> stockTypeIds;

    @NotNull(message = "최소 운용 금액을 입력해주세요.")
    private MinimumInvestmentAmount minimumInvestmentAmount;

    private ProposalFileDto proposalFile;

    @NotNull(message = "전략 소개를 입력해주세요.")
    @NotBlank(message = "전략 소개는 비어 있을 수 없습니다.")
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
