package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.strategy.dto.request.TradeTypeRequestDTO;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class TradeTypeServiceTest {
    @Autowired
    private TradeTypeService tradeTypeService;

    private ArrayList<TradeTypeRequestDTO> tradeTypeRequestDtoList;

    @BeforeEach
    void setUp() {
        tradeTypeRequestDtoList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            TradeTypeRequestDTO tradetype = TradeTypeRequestDTO.builder()
                    .tradeTypeName("Sample_Trade" + i)
                    .tradeTypeIconURL(String.format("/icons/sample-icon%d.png", i))
                    .size(1200)
                    .build();

            tradeTypeRequestDtoList.add(tradetype);
        }
    }

    @Test
    @DisplayName("매매유형 등록 테스트")
    void registerTradeType() {
        TradeTypeRequestDTO tradeType = tradeTypeRequestDtoList.get(0);
        String savedTradeType = tradeTypeService.saveTradeType(tradeType);
        assertThat(savedTradeType).isNotNull();
        System.out.println("savedTradeType: " + savedTradeType);
    }
}
