package com.investmetic.global.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileUploadRequestDto {
    private String fileName;
    private int fileSize;

    @Builder
    public FileUploadRequestDto(String fileName, int fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
}