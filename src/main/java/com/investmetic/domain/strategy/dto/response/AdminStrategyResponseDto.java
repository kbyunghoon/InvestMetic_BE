package com.investmetic.domain.strategy.dto.response;

import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.model.IsPublic;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AdminStrategyResponseDto {

    private Long strategyId;                        // 전략 ID
    private String strategyName;                    // 전략명
    private String nickname;                        // 트레이더 이름
    private IsPublic isPublic;                      //공개 여부
    private IsApproved isApproved;                  //승인 여부
    private LocalDateTime createAt;

    @QueryProjection
    public AdminStrategyResponseDto(LocalDateTime createAt, Long strategyId, String strategyName, String nickname,
                                    IsPublic isPublic, IsApproved isApproved) {
        this.createAt = createAt;
        this.strategyId = strategyId;
        this.strategyName = strategyName;
        this.nickname = nickname;
        this.isPublic = isPublic;
        this.isApproved = isApproved;
    }

}
