package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class TradeType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeTypeId;
    private String tradeName;
    private boolean activate_state;

    @Column(length = 1000)
    private String tradeIconPath;

    // 매매유형 임시용 생성자입니다. 충돌시 아래 생성코드는 삭제해주시고, 작성하신것으로 사용해주세요 -오정훈-
    @Builder
    public TradeType(Long tradeTypeId, String tradeName, boolean activate_state, String tradeIconPath) {
        this.tradeTypeId = tradeTypeId;
        this.tradeName = tradeName;
        this.activate_state = activate_state;
        this.tradeIconPath = tradeIconPath;
    }
}