package com.investmetic.global.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProposalFileDto {
    private String proposalFileName;
    private int proposalFileSize;

    @Builder
    public ProposalFileDto(String proposalFileName, int proposalFileSize) {
        this.proposalFileName = proposalFileName;
        this.proposalFileSize = proposalFileSize;
    }
}