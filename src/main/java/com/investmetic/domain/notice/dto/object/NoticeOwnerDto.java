package com.investmetic.domain.notice.dto.object;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeOwnerDto {

    private Long userId;
    private String nickname;

    @Builder
    @QueryProjection
    public NoticeOwnerDto (Long userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }
}
