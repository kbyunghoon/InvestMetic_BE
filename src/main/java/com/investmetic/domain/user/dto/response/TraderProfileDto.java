package com.investmetic.domain.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class TraderProfileDto {

    private long userId;
    private String userName;
    private String nickname;
    private String imageUrl;
    private long strategyCount;
    private long totalSubCount;


    @QueryProjection
    public TraderProfileDto(long userId, String userName, String nickname, String imageUrl, long strategyCount,
                            long totalSubCount) {
        this.userId = userId;
        this.userName = userName;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.strategyCount = strategyCount;
        this.totalSubCount = totalSubCount;
    }
}
