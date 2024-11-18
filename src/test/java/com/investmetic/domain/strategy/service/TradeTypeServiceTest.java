package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.strategy.dto.request.TradeTypeRequestDTO;
import com.investmetic.domain.strategy.dto.response.StockTypeResponseDTO;
import com.investmetic.domain.strategy.dto.response.TradeTypeResponseDTO;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.global.common.PageResponseDto;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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
class TradeTypeServiceTest {
    @Autowired
    private TradeTypeService tradeTypeService;
    @Autowired
    private TradeTypeRepository tradeTypeRepository;

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
    @Test
    @DisplayName("매매유형 상태 변경 테스트")
    void changeStockTypes() {
        // 페이지 조회(true) : 활성 상태 페이지 불러오기
        Pageable pageable = PageRequest.of(0, 10);
        List<TradeTypeResponseDTO> dtolist=tradeTypeService.getTradeTypes( true);

        // 페이지 첫번째 dto 가져오기
        TradeTypeResponseDTO dto=dtolist.get(0);
        tradeTypeService.changeActivateState(dto.getTradeTypeId());
        TradeType tradeType=tradeTypeRepository.findByTradeTypeId(dto.getTradeTypeId()).orElse(null);
        assertThat(tradeType.getActivateState()).isEqualTo(false);
    }
}
