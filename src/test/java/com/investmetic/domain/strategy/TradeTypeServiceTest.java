package com.investmetic.domain.strategy;

import com.investmetic.domain.strategy.dto.request.TradeTypeRequestDTO;
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

    private ArrayList<TradeTypeRequestDTO> tradeTypeRequestDtoList;

    @BeforeEach
    void setUp() {
        tradeTypeRequestDtoList = new ArrayList<TradeTypeRequestDTO>();

        for (int i = 1; i <= 5; i++) {
            TradeTypeRequestDTO tradetype = TradeTypeRequestDTO.builder()
                    .tradeName("Sample_Trade" + i)
                    .tradeIconURL(String.format("/icons/sample-icon%d.png", i))
                    .size(1200)
                    .build();

            tradeTypeRequestDtoList.add(tradetype);
        }
    }

    @Test
    @DisplayName("매매유형 등록 테스트")
    public void registerTradeType() {
        TradeTypeRequestDTO tradeType = tradeTypeRequestDtoList.get(0);
        String savedTradeType = tradeTypeService.saveTradeType(tradeType);
        assertThat(savedTradeType).isNotNull();
        System.out.println("savedTradeType: " + savedTradeType);
        assertThat(savedTradeType.split(".png")[0]+".png").isEqualTo(tradeType.getTradeIconURL());
    }
}
