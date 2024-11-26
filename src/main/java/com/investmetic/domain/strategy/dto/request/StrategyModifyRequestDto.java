package com.investmetic.domain.strategy.dto.request;

import com.investmetic.global.dto.ProposalFileDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StrategyModifyRequestDto {
    private String strategyName; // 전략명
    private ProposalFileDto proposalFile; // 제안서 파일 정보
    private String description;
    private Boolean proposalModified;

    @Builder
    public StrategyModifyRequestDto(String strategyName, ProposalFileDto proposalFile, String description,
                                    Boolean proposalModified) {
        this.strategyName = strategyName;
        this.proposalFile = proposalFile;
        this.description = description;
        this.proposalModified = proposalModified;
    }
}
