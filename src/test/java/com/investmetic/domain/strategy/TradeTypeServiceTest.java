package com.investmetic.domain.strategy;

import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.service.TradeTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TradeTypeServiceTest {
    @Autowired
    private TradeTypeService tradeTypeService;

    private ArrayList<TradeType> tradetypeList;

    @BeforeEach
    void setUp() {
        tradetypeList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            TradeType tradetype = TradeType.builder()
                    .tradeName("Sample Trade " + i)
                    .activateState(true)  // 필드명이 activate_state라면 확인하여 맞춰주세요
                    .tradeIconPath(String.format("/icons/sample-icon%d.png", i))
                    .build();

            tradetypeList.add(tradetype);
        }
    }

    @Test
    @DisplayName("매매유형 등록 테스트")
    public void registerTradeType() {
        TradeType tradeType = tradetypeList.get(0);
        String savedTradeType = tradeTypeService.saveTradeType(tradeType, 1200);
        assertThat(savedTradeType).isNotNull();

    }
}
