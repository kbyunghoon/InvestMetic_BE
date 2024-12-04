package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.response.TotalStrategyMetricsResponseDto;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

//Todo : 테스트 코드 작성 및 개선 예정
@SpringBootTest
@Transactional
public class MainPageServiceTest {
    @Autowired
    private MainPageService mainPageService;

    @Test
    @DisplayName("대표 통합 지표 조회 테스트")
    public void test() {
        TotalStrategyMetricsResponseDto dto =mainPageService.getMetricsByDateRange();
        System.out.println(dto.getData());
        System.out.println(dto.getDates());
        System.out.println(dto.getDates().size());
    }
}
