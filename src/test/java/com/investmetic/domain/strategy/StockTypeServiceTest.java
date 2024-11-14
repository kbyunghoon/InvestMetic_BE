package com.investmetic.domain.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.strategy.dto.request.StockTypeRequestDTO;

import com.investmetic.domain.strategy.service.StockTypeService;
import com.investmetic.global.util.s3.S3FileService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockTypeServiceTest {
    @Autowired
    private StockTypeService stockTypeService;

    private ArrayList<StockTypeRequestDTO> stockTypeRequestList;
    @Autowired
    private S3FileService s3FileService;

    @BeforeEach
    void setUp() {
        stockTypeRequestList = new ArrayList<StockTypeRequestDTO>();

        for (int i = 1; i <= 5; i++) {
            StockTypeRequestDTO stockTypeRequestDTO = StockTypeRequestDTO.builder()
                    .stockTypeName("Sample_Stock_Type" + i)
                    .stockTypeIconURL(String.format("/icons/sampleStock-icon%d.png", i))
                    .size(1200)
                    .build();
            stockTypeRequestList.add(stockTypeRequestDTO);
        }
    }

    @Test
    @DisplayName("종목 조회 테스트")
    void getAllStockTypes() {

    }

    @Test
    @DisplayName("종목 등록 테스트")
    void registerTradeType() {
        StockTypeRequestDTO stockType = stockTypeRequestList.get(0);
        String savedStockType = stockTypeService.saveStockType(stockType);
        assertThat(savedStockType).isNotNull();

    }
}
