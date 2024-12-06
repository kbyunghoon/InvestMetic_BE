package com.investmetic.domain.notice.dto.response;

import com.investmetic.domain.notice.dto.object.NoticeOwnerDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeListDto {

    private Long noticeId;
    private NoticeOwnerDto user;
    private String title;
    private String content;
    private String createdAt;

    @Builder
    @QueryProjection
    public NoticeListDto(Long noticeId, NoticeOwnerDto user, String title, String content, String createdAt) {
        this.noticeId = noticeId;
        this.user = user;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }
}
