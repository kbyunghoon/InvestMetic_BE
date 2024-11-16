package com.investmetic.global.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MultiPresignedUrlResponseDto {
    private final List<PresignedUrlResponseDto> presignedUrls; // 여러 개의 Presigned URL을 포함한 리스트

    @Builder
    public MultiPresignedUrlResponseDto(List<PresignedUrlResponseDto> presignedUrls) {
        this.presignedUrls = presignedUrls;
    }
}
