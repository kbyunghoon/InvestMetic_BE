package com.investmetic.domain.strategy.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountImageRequestDto {
    private String fileName;
    private int fileSize;
    private String title;


    @Builder
    public AccountImageRequestDto(String fileName, int fileSize, String title) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.title = title;
    }
}