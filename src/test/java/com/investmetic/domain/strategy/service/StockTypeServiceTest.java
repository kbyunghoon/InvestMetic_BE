package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.strategy.dto.request.StockTypeRequestDTO;

import com.investmetic.domain.strategy.dto.response.StockTypeResponseDTO;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
@Transactional
class StockTypeServiceTest {
    @Autowired
    private StockTypeService stockTypeService;

    private ArrayList<StockTypeRequestDTO> stockTypeRequestList;
    @Autowired
    private S3FileService s3FileService;
    @Autowired
    private StockTypeRepository stockTypeRepository;

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
        stockTypeService.saveStockType(stockTypeRequestList.get(0));
    }

    @Test
    @DisplayName("종목 등록 테스트")
    void registerTradeType() {
        StockTypeRequestDTO stockType = stockTypeRequestList.get(0);
        String savedStockType = stockTypeService.saveStockType(stockType);
        assertThat(savedStockType).isNotNull();
    }
    @Test
    @DisplayName("종목 조회 테스트")
    void getStockType() {
        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<StockTypeResponseDTO> stocks=stockTypeService.getStockTypes(pageable, true);
        // 생성할 때 넣은 종목 이름과 비교
        assertThat(stocks.getContent().get(0).getStockTypeName()).isEqualTo(stockTypeRequestList.get(0).getStockTypeName());
    }
    @Test
    @DisplayName("종목 상태 변경 테스트")
    void changeStockTypes() {
        // 페이지 조회(true) : 활성 상태 페이지 불러오기
        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<StockTypeResponseDTO> dto=stockTypeService.getStockTypes(pageable, true);

        // 페이지 첫번째 dto 가져오기
        StockTypeResponseDTO changeDto=dto.getContent().get(0);
        stockTypeService.changeActivateState(changeDto.getStockTypeId());
        StockType stockType=stockTypeRepository.findByStockTypeId(changeDto.getStockTypeId()).orElse(null);
        assertThat(stockType.getActivateState()).isEqualTo(false);
    }
}
