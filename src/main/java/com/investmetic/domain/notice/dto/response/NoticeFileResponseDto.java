package com.investmetic.domain.notice.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class NoticeFileResponseDto {
    private Long Notice_File_Id;
    private String fileName;
    @QueryProjection
    public NoticeFileResponseDto(Long Notice_File_Id, String fileName) {
        this.Notice_File_Id = Notice_File_Id;
        this.fileName = fileName;
    }
}
