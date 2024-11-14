package com.investmetic.global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class PresignedUrlResponseDto {
    private final String presignedUrl;     // S3에 파일 업로드를 위한 Presigned URL

    @Builder
    public PresignedUrlResponseDto(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }
}
