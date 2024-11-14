package com.investmetic.domain.strategy;

import com.investmetic.domain.strategy.dto.response.StockTypeResponseDTO;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.global.common.PageResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
public class StockTypeRepositoryTest {
    @Autowired
    private StockTypeRepository stockTypeRepository;

    @Test
    @DisplayName("활성 상태 목록 조회 테스트")
    void findActivateStateStocks() {
        PageRequest pageable=PageRequest.of(0,10);
        Page<StockTypeResponseDTO> stocks = stockTypeRepository.findByactivateState(true, pageable).map(
                StockTypeResponseDTO::from);
        PageResponseDto<StockTypeResponseDTO> dtolist = new PageResponseDto<>(stocks);
        System.out.println(dtolist.getTotalElements());
    }
}
