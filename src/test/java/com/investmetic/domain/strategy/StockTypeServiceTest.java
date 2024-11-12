package com.investmetic.domain.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.strategy.model.entity.StockType;

import com.investmetic.domain.strategy.service.StockTypeService;
import com.investmetic.global.util.s3.S3FileService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StockTypeServiceTest {
    @Autowired
    private StockTypeService stockTypeService;

    private ArrayList<StockType> stockTypeList;
    @Autowired
    private S3FileService s3FileService;

    @BeforeEach
    void setUp() {
        stockTypeList = new ArrayList<StockType>();

        for (int i = 1; i <= 5; i++) {
            StockType stocktype = StockType.builder()
                    .stockTypeName("Sample_Stock_Type" + i)
                    .activateState(true)  // 필드명이 activate_state라면 확인하여 맞춰주세요
                    .stockTypeIconURL(String.format("/icons/sampleStock-icon%d.png", i))
                    .build();

            stockTypeList.add(stocktype);
        }
    }

    @Test
    @DisplayName("종목 등록 테스트")
    public void registerTradeType() {
        StockType stockType = stockTypeList.get(0);
        String savedStockType = stockTypeService.saveStockType(stockType, 1200);
        assertThat(savedStockType).isNotNull();
        assertThat(savedStockType.split(".png")[0]+".png").isEqualTo(stockType.getStockTypeIconURL());
    }
}
