package com.investmetic.domain.notice.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class NoticeDetailResponseDto {
    private String title;
    private String content;
    private List<NoticeFileResponseDto> files;

    @QueryProjection
    public NoticeDetailResponseDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public void updateFiles(List<NoticeFileResponseDto> files){
        this.files = files;
    }
}
