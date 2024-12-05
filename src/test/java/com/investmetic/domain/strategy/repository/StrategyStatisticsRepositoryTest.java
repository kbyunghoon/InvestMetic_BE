package com.investmetic.domain.strategy.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StrategyStatisticsRepositoryTest {

    @Autowired
    private StrategyStatisticsRepository strategyStatisticsRepository;

    @Autowired
    private EntityManager em;

    private List<Long> strategyStatisticIds = new ArrayList<>();

    @BeforeEach
    void setUp() {

        // 테스트 데이터 초기화 ( 실제수치 말고 극단적으로 값선택)
        for (int i = 0; i < 3; i++) {
            StrategyStatistics stats = StrategyStatistics.builder()
                    .maxDrawdownRate(10000.0 - i) // MDD (작을수록 랭킹이 높음)
                    .dailyProfitLossStdDev(10000.0 - i) // 수익률 표준편차 (작을수록 랭킹이 높음)
                    .winRate(10000.0 - i) // 승률 (클수록 랭킹이 높음)
                    .build();

            // DB에 저장
            strategyStatisticIds.add(strategyStatisticsRepository.save(stats).getStrategyStatisticsId());
        }

        em.flush();
        em.clear();
    }


    @DisplayName("전략 랭킹 업데이트 테스트")
    @Test
    void testUpdateRanks() {
        // Given: 저장된 ID를 기반으로 데이터 조회
        List<StrategyStatistics> beforeUpdate = strategyStatisticIds.stream()
                .map(id -> strategyStatisticsRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.STRATEGY_NOT_FOUND)))
                .toList();

        // 초기 상태에서 랭킹이 null이어야 함
        beforeUpdate.forEach(stat -> {
            assertNull(stat.getMddRank());
            assertNull(stat.getStdDevRank());
            assertNull(stat.getWinRateRank());
        });

        // When: 랭킹 업데이트 실행
        strategyStatisticsRepository.updateRanks();

        em.flush();
        em.clear();

        // Then: 업데이트된 데이터 조회 및 검증
        List<StrategyStatistics> afterUpdate = strategyStatisticIds.stream()
                .map(id -> strategyStatisticsRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.STRATEGY_NOT_FOUND)))
                .toList();

        // MDD 랭킹 검증 (작은 maxDrawdownRate 순)
        int strategyStatisticsSize = strategyStatisticsRepository.findAll().size();
        assertThat(afterUpdate.get(0).getMddRank()).isEqualTo(strategyStatisticsSize); // 꼴등
        assertThat(afterUpdate.get(1).getMddRank()).isEqualTo(strategyStatisticsSize - 1); // 꼴등에서 2번째
        assertThat(afterUpdate.get(2).getMddRank()).isEqualTo(strategyStatisticsSize - 2); // 꼴등에서 3번째

        // 수익률 표준편차 검증 (작은 dailyProfitLossStdDev 순)
        assertThat(afterUpdate.get(0).getStdDevRank()).isEqualTo(strategyStatisticsSize); // 꼴등
        assertThat(afterUpdate.get(1).getStdDevRank()).isEqualTo(strategyStatisticsSize - 1); // 꼴등에서 2번째
        assertThat(afterUpdate.get(2).getStdDevRank()).isEqualTo(strategyStatisticsSize - 2); // 꼴등에서 3번째

        // 승률 검증 (큰 winRate 순)
        assertThat(afterUpdate.get(0).getWinRateRank()).isEqualTo(1); // 무조건 1등
        assertThat(afterUpdate.get(1).getWinRateRank()).isEqualTo(2); // 무조건 2등
        assertThat(afterUpdate.get(2).getWinRateRank()).isEqualTo(3); // 무조건 3등
    }
}
