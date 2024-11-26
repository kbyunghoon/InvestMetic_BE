package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.dto.response.DailyAnalysisResponse;
import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Proceed;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MyDailyAnalysisServiceTest {

    @Autowired
    private StrategyAnalysisService strategyAnalysisService;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private TradeTypeRepository tradeTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyAnalysisRepository dailyAnalysisRepository;

    @Autowired
    private EntityManager em;

    private Strategy strategy;

    @BeforeEach
    void setUp() {
        // 사용자 생성 및 영속화
        User user = TestEntityFactory.createTestUser();
        user = userRepository.save(user); // 영속화

        // 거래 유형 생성 및 영속화
        TradeType tradeType = TestEntityFactory.createTestTradeType();
        tradeType = tradeTypeRepository.save(tradeType); // 영속화

        // 전략 생성 및 영속화
        strategy = TestEntityFactory.createTestStrategy(user, tradeType);
        strategy = strategyRepository.save(strategy);

        em.flush(); // 데이터베이스에 반영
        em.clear(); // 영속성 컨텍스트 초기화
    }

    @Test
    @DisplayName("dailyDate가 중복일 때 Proceed가 NO인지 테스트")
    void 테스트1() {

        LocalDate now = LocalDate.now();

        DailyAnalysis dailyAnalysis1 = DailyAnalysis.builder()
                .strategy(strategy)
                .dailyDate(now)
                .proceed(Proceed.NO)
                .principal(1000L)
                .build();

        DailyAnalysis dailyAnalysis2 = DailyAnalysis.builder()
                .strategy(strategy)
                .dailyDate(now)
                .proceed(Proceed.YES)
                .principal(2000L)
                .build();

        dailyAnalysisRepository.save(dailyAnalysis1);
        dailyAnalysisRepository.save(dailyAnalysis2);

        em.flush();
        em.clear();

        Pageable pageable = PageRequest.of(0, 10);

        PageResponseDto<DailyAnalysisResponse> result = strategyAnalysisService.getMyDailyAnalysis(
                strategy.getStrategyId(), pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        // proceed가 NO인 일간분석의 principal 반환
        assertThat(result.getContent())
                .anyMatch(response -> response.getDailyDate().equals(now) && response.getPrincipal() == 1000L);
    }


    @Test
    @DisplayName("dailyDate가 최신순으로 출력되는지 테스트")
    void 테스트3() {
        // Given: 두 개의 서로 다른 날짜 데이터를 생성
        LocalDate date1 = LocalDate.of(2024, 11, 1); // 첫 번째 날짜
        LocalDate date2 = LocalDate.of(2024, 11, 2); // 두 번째 날짜

        DailyAnalysis dailyAnalysis1 = DailyAnalysis.builder()
                .strategy(strategy)
                .dailyDate(date1) // 날짜가 더 이전
                .proceed(Proceed.NO)
                .principal(1000L)
                .build();

        DailyAnalysis dailyAnalysis2 = DailyAnalysis.builder()
                .strategy(strategy)
                .dailyDate(date2) // 날짜가 더 최신
                .proceed(Proceed.YES)
                .principal(2000L)
                .build();

        dailyAnalysisRepository.save(dailyAnalysis1);
        dailyAnalysisRepository.save(dailyAnalysis2);

        em.flush();
        em.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // When: 서비스 메서드 호출
        PageResponseDto<DailyAnalysisResponse> result = strategyAnalysisService.getMyDailyAnalysis(
                strategy.getStrategyId(), pageable);

        // Then: 결과 검증
        // 반환된 결과가 null이 아님을 확인
        assertThat(result).isNotNull();

        // 반환된 내용이 비어 있지 않음을 확인
        assertThat(result.getContent()).isNotEmpty();

        // 반환된 dailyDate 목록 추출
        List<LocalDate> dailyDates = result.getContent().stream()
                .map(DailyAnalysisResponse::getDailyDate) // 날짜만 추출
                .collect(Collectors.toList());

        // 날짜가 최신순으로 정렬되었는지 확인
        assertThat(dailyDates).isSortedAccordingTo(Comparator.reverseOrder());

    }


}