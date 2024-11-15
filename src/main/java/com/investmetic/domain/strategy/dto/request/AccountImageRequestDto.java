package com.investmetic.domain.strategy.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountImageRequestDto {
    private String fileName;
    private int fileSize;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String date;


    @Builder
    public AccountImageRequestDto(String fileName, int fileSize, String date) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.date = date;
    }
}