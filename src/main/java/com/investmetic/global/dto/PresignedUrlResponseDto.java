package com.investmetic.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlResponseDto {
    private String presignedUrl;     // S3에 파일 업로드를 위한 Presigned URL
}
