package com.investmetic.domain.strategy.dto.request;

import com.investmetic.global.dto.ProposalFileDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StrategyModifyRequestDto {
    @NotNull(message = "전략명을 입력해주세요.")
    @NotBlank(message = "전략명은 비어 있을 수 없습니다.")
    private String strategyName; // 전략명

    private ProposalFileDto proposalFile; // 제안서 파일 정보

    @NotNull(message = "전략 소개를 입력해주세요.")
    @NotBlank(message = "전략 소개는 비어 있을 수 없습니다.")
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
