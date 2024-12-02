package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.dto.response.StrategyAnalysisResponse;
import com.investmetic.domain.strategy.model.AnalysisOption;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

//TODO : 테스트코드 추가 및 개선 예정
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class StrategyDetailServiceTest {


    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TradeTypeRepository tradeTypeRepository;

    @Autowired
    private StrategyDetailService strategyDetailService;

    private Strategy testStrategy;
    private User testUser;
    private TradeType testTradeType;

    @BeforeEach
    public void setup() {
        testUser = userRepository.save(TestEntityFactory.createTestUser());
        testTradeType = tradeTypeRepository.save(TestEntityFactory.createTestTradeType());
        testStrategy = strategyRepository.save(TestEntityFactory.createTestStrategy(testUser, testTradeType));
    }

    @Test
    @DisplayName("옵션이 null일 때 BusinessException 발생 테스트")
    void strategyAnalysisTest1() {

        assertThatThrownBy(() -> strategyDetailService.getStrategyAnalysis(testStrategy.getStrategyId(), null,
                AnalysisOption.BALANCE))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ANALYSIS_OPTION_NOT_FOUND.getMessage());

        assertThatThrownBy(
                () -> strategyDetailService.getStrategyAnalysis(testStrategy.getStrategyId(), AnalysisOption.BALANCE,
                        null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ANALYSIS_OPTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("x축과 y축 데이터의 사이즈가 같은지 테스트")
    void strategyAnalysisTest2() {
        StrategyAnalysisResponse response = strategyDetailService.getStrategyAnalysis(
                testStrategy.getStrategyId(), AnalysisOption.BALANCE, AnalysisOption.PRINCIPAL);

        // x축 y축 사이즈 같아야함
        assertThat(response.getDates())
                .hasSize(response.getData().get(AnalysisOption.BALANCE.name()).size());

        // PRINCIPAL의 이름이 포함되어야함
        assertThat(response.getDates())
                .hasSize(response.getData().get(AnalysisOption.PRINCIPAL.name()).size());
    }

    @Test
    @DisplayName("옵션이 두 개 같을 때 y축 데이터 하나만 나오는지 테스트")
    void strategyAnalysisTest3() {
        StrategyAnalysisResponse response = strategyDetailService.getStrategyAnalysis(
                testStrategy.getStrategyId(), AnalysisOption.BALANCE, AnalysisOption.BALANCE);

        assertThat(response.getData()).hasSize(1);

        assertThat(response.getData()).containsKey(AnalysisOption.BALANCE.name());
    }

    @Test
    @DisplayName("옵션이 다를 때 y축 데이터 두 개가 나오는지 테스트")
    void strategyAnalysisTest4() {
        StrategyAnalysisResponse response = strategyDetailService.getStrategyAnalysis(
                testStrategy.getStrategyId(), AnalysisOption.BALANCE, AnalysisOption.PRINCIPAL);

        assertThat(response.getData()).hasSize(2);

        assertThat(response.getData()).containsKeys(AnalysisOption.BALANCE.name(), AnalysisOption.PRINCIPAL.name());
    }

}