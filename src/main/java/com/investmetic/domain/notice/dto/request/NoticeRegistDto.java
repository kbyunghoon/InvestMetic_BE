package com.investmetic.domain.notice.dto.request;

import com.investmetic.domain.notice.model.entity.Notice;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NoticeRegistDto {
    private String title;
    private String content;
    private List<Integer> sizes;
    private List<String> filePaths;
    public Notice toEntity(){
        return Notice.builder()
                .title(title)
                .content(content)
                .build();
    }
}
