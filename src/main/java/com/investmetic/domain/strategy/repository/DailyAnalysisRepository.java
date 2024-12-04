package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyAnalysisRepository extends JpaRepository<DailyAnalysis, Long>, DailyAnalysisRepositoryCustom {
    List<DailyAnalysis> findByStrategy(Strategy strategyId);

    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId ORDER BY d.dailyDate ASC")
    List<DailyAnalysis> findByStrategyId(Long strategyId);

    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy = :strategy AND d.dailyDate = :dailyDate AND d.proceed = 'NO'")
    Optional<DailyAnalysis> findByStrategyAndDailyDateAndProceedIsFalse(
            @Param("strategy") Strategy strategy,
            @Param("dailyDate") LocalDate dailyDate);

    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy = :strategy AND d.dailyDate = :dailyDate AND d.proceed = 'YES'")
    Optional<DailyAnalysis> findByStrategyAndDailyDateAndProceedIsTrue(
            @Param("strategy") Strategy strategy,
            @Param("dailyDate") LocalDate dailyDate);

    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy = :strategy AND d.dailyDate = :dailyDate " +
            "ORDER BY CASE WHEN d.proceed = 'NO' THEN 1 ELSE 2 END LIMIT 1")
    Optional<DailyAnalysis> findDailyAnalysisByStrategyAndDate(@Param("strategy") Strategy strategy,
                                                               @Param("dailyDate") LocalDate dailyDate);

    // 특정 전략의 해당 날짜의 이전 데이터들 가져오기
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId AND d.dailyDate <= :startDate ORDER BY d.dailyDate ASC")
    List<DailyAnalysis> findAllByStrategyAndDateBefore(@Param("strategyId") Long strategyId,
                                                       @Param("startDate") LocalDate startDate);

    // 특정 전략의 가장 오래된 updated_at 이후 데이터를 가져오기
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId AND d.dailyDate >= :startDate ORDER BY d.dailyDate ASC")
    List<DailyAnalysis> findAllByStrategyAndDateAfter(@Param("strategyId") Long strategyId,
                                                      @Param("startDate") LocalDate startDate);

    // 특정 전략의 가장 오래된 updated_at 날짜 조회
    @Query("SELECT MIN(d.updatedAt) FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId AND d.updatedAt > :lastRunDate")
    Optional<LocalDateTime> findOldestUpdatedAtAfter(@Param("strategyId") Long strategyId,
                                                     @Param("lastRunDate") LocalDateTime lastRunDate);

    // 전날 데이터 조회
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId AND d.dailyDate < :currentDate ORDER BY d.dailyDate DESC LIMIT 1")
    Optional<DailyAnalysis> findLatestBefore(@Param("strategyId") Long strategyId,
                                             @Param("currentDate") LocalDate currentDate);

    /**
     * 특정 전략 ID로 DailyAnalysis 데이터를 조회
     */
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId ORDER BY d.dailyDate ASC")
    List<DailyAnalysis> findAllByStrategy(@Param("strategyId") Long strategyId);

    @Query("SELECT d FROM DailyAnalysis d WHERE d.proceed = 'NO'")
    List<DailyAnalysis> findAllByProceedIsFalse();

    @Query("""
            SELECT d FROM DailyAnalysis d
                WHERE d.proceed = 'NO'
                  AND d.dailyDate = (
                      SELECT MIN(d2.dailyDate) FROM DailyAnalysis d2
                      WHERE d2.strategy.strategyId = d.strategy.strategyId
                        AND d2.proceed = 'NO'
                  )
                ORDER BY d.dailyDate ASC
            """)
    List<DailyAnalysis> findEligibleDailyAnalysis();

    // 특정 개수 데이터 조회
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId AND d.dailyDate < :currentDate ORDER BY d.dailyDate DESC LIMIT :limitCount")
    List<DailyAnalysis> findSpecificDailyAnalyses(@Param("strategyId") Long strategyId,
                                                  @Param("currentDate") LocalDate currentDate,
                                                  @Param("limitCount") Long limitCount);

    @Query("SELECT d.kpRatio FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId ORDER BY d.dailyDate DESC LIMIT 1")
    Optional<Double> findLatestKpRatioByStrategyId(@Param("strategyId") Long strategyId);

    // 대표 전략 통합전략 지표 조회
    @Query(value = """

            SELECT
            DA.daily_date,
            AVG(DA.reference_price) AS avg_reference_price,
            coalesce((
                SELECT DA2.reference_price
                FROM daily_analysis AS DA2
                         JOIN strategy AS st ON DA2.strategy_id = st.strategy_id
                WHERE st.strategy_id = (
                    SELECT st1.strategy_id
                    FROM strategy AS st1
                    WHERE st1.sm_score = (
                        SELECT MAX(st2.sm_score) FROM strategy AS st2
                    )
                    LIMIT 1
                )
                  AND DA2.daily_date = DA.daily_date
            ),0) AS highest_sm_score_reference_price,
            coalesce((
                SELECT DA2.reference_price
                FROM daily_analysis AS DA2
                         JOIN strategy AS st ON DA2.strategy_id = st.strategy_id
                WHERE st.strategy_id = (
                    SELECT st1.strategy_id
                    FROM strategy AS st1
                    WHERE st1.subscription_count = (
                        SELECT MAX(st2.subscription_count) FROM strategy AS st2
                    )
                    LIMIT 1
                )
                  AND DA2.daily_date = DA.daily_date
            ),0) AS highest_subscribe_score_reference_price
        FROM
            daily_analysis AS DA
        WHERE
            DA.daily_date BETWEEN :startDate AND :endDate
        GROUP BY
            DA.daily_date
        """, nativeQuery = true)
    List<Object[]> findMetricsByDateRange(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    boolean existsByStrategyAndDailyDate(Strategy strategy, LocalDate dailyDate);

    void deleteAllByStrategy(Strategy strategy);

    boolean existsByStrategyAndDailyAnalysisId(Strategy strategy, Long analysisId);

    void deleteByStrategyAndDailyAnalysisId(Strategy strategy, Long analysisId);
}