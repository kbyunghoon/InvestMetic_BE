package com.investmetic.domain.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TraderProfileDto {

    private Long userId;
    private String userName;
    private String nickname;
    private String imageUrl;
    private Long strategyCount;
    private Integer totalSubCount;


    @QueryProjection
    public TraderProfileDto(Long userId, String userName, String nickname, String imageUrl, Long strategyCount,
                            Integer totalSubCount) {
        this.userId = userId;
        this.userName = userName;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.strategyCount = strategyCount;
        this.totalSubCount = totalSubCount;
    }
}
