package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.investmetic.domain.strategy.dto.response.StrategyAnalysisResponse;
import com.investmetic.domain.strategy.model.AnalysisOption;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class StrategyDetailServiceTest {

    @Autowired
    private StrategyDetailService strategyDetailService;

    @Test
    @DisplayName("옵션이 null일 때 BusinessException 발생 테스트")
    void strategyAnalysisTest1() {
        assertThatThrownBy(() -> strategyDetailService.getStrategyAnalysis(1L, null, AnalysisOption.BALANCE))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ANALYSIS_OPTION_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> strategyDetailService.getStrategyAnalysis(1L, AnalysisOption.BALANCE, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ANALYSIS_OPTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("x축과 y축 데이터의 사이즈가 같은지 테스트")
    void strategyAnalysisTest2() {
        StrategyAnalysisResponse response = strategyDetailService.getStrategyAnalysis(
                1L, AnalysisOption.BALANCE, AnalysisOption.PRINCIPAL);

        // x축 y축 사이즈 같아야함
        assertThat(response.getXAxis())
                .hasSize(response.getYAxis().get(AnalysisOption.BALANCE.name()).size());

        // PRINCIPAL의 이름이 포함되어야함
        assertThat(response.getXAxis())
                .hasSize(response.getYAxis().get(AnalysisOption.PRINCIPAL.name()).size());
    }

    @Test
    @DisplayName("옵션이 두 개 같을 때 y축 데이터 하나만 나오는지 테스트")
    void strategyAnalysisTest3() {
        StrategyAnalysisResponse response = strategyDetailService.getStrategyAnalysis(
                1L, AnalysisOption.BALANCE, AnalysisOption.BALANCE);

        assertThat(response.getYAxis()).hasSize(1);

        assertThat(response.getYAxis()).containsKey(AnalysisOption.BALANCE.name());
    }

    @Test
    @DisplayName("옵션이 다를 때 y축 데이터 두 개가 나오는지 테스트")
    void strategyAnalysisTest4() {
        StrategyAnalysisResponse response = strategyDetailService.getStrategyAnalysis(
                1L, AnalysisOption.BALANCE, AnalysisOption.PRINCIPAL);

        assertThat(response.getYAxis()).hasSize(2);

        assertThat(response.getYAxis()).containsKeys(AnalysisOption.BALANCE.name(), AnalysisOption.PRINCIPAL.name());
    }

}