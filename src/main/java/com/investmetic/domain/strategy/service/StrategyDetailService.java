package com.investmetic.domain.strategy.service;

import com.investmetic.domain.accountverification.dto.response.AccountImagesResponseDto;
import com.investmetic.domain.accountverification.repository.AccountVerificationRepository;
import com.investmetic.domain.strategy.dto.AnalysisDataDto;
import com.investmetic.domain.strategy.dto.response.DailyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.MonthlyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.MyStrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.StrategyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.statistic.StrategyStatisticsResponse;
import com.investmetic.domain.strategy.model.AnalysisOption;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.StrategyStatistics;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.MonthlyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//TODO : 추후 조회 성능 개선필요
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StrategyDetailService {
    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final MonthlyAnalysisRepository monthlyAnalysisRepository;
    private final StrategyRepository strategyRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AccountVerificationRepository accountVerificationRepository;

    /**
     * 전략 통계 조회
     */
    public StrategyStatisticsResponse getStatistics(Long strategyId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        StrategyStatistics stats = strategy.getStrategyStatistics();
        if (stats == null) {
            throw new BusinessException(ErrorCode.STATISTICS_NOT_FOUND);
        }

        return StrategyStatisticsResponse.from(stats);
    }

    /**
     * 일간 분석 데이터 조회
     */
    public PageResponseDto<DailyAnalysisResponse> getDailyAnalysis(Long strategyId, Pageable pageable) {
        validateStrategyExists(strategyId);
        Page<DailyAnalysisResponse> page = dailyAnalysisRepository.findByStrategyId(strategyId, pageable);
        return new PageResponseDto<>(page);
    }

    /**
     * 월간 분석 데이터 조회
     */
    public PageResponseDto<MonthlyAnalysisResponse> getMonthlyAnalysis(Long strategyId, Pageable pageable) {
        validateStrategyExists(strategyId);
        Page<MonthlyAnalysisResponse> page = monthlyAnalysisRepository.findByStrategyId(strategyId, pageable)
                .map(MonthlyAnalysisResponse::from);

        return new PageResponseDto<>(page);
    }

    /**
     * 전략 상세 데이터 조회
     */
    public StrategyDetailResponse getStrategyDetail(Long strategyId, Long userId) {
        validateStrategyExists(strategyId);

        StrategyDetailResponse strategyDetail = strategyRepository.findStrategyDetail(strategyId);

        // 구독여부 체크
        boolean isSubscribed = subscriptionRepository.existsByStrategyIdAndUserId(strategyId, userId);

        // 구독여부 업데이트
        strategyDetail.updateIsSubscribed(isSubscribed);

        // todo 임시용 쓰레기코드 (수정해야함)
        // 제안서 여부 판단
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        if (strategy.getProposalFilePath() == null) {
            strategyDetail.updateHasProposal(false);
        } else {
            strategyDetail.updateHasProposal(true);
        }

        return strategyDetail;
    }

    /**
     * 나의 전략 상세 데이터 조회
     */
    public MyStrategyDetailResponse getMyStrategyDetail(Long strategyId) {
        validateStrategyExists(strategyId);

        MyStrategyDetailResponse myStrategyDetail = strategyRepository.findMyStrategyDetail(strategyId);

        // todo 임시용 쓰레기코드 (수정해야함)
        // 제안서 여부 판단
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        if (strategy.getProposalFilePath() == null) {
            myStrategyDetail.updateHasProposal(false);
        } else {
            myStrategyDetail.updateHasProposal(true);
        }

        return myStrategyDetail;
    }

    /**
     * 전략 분석 데이터 조회
     */
    public StrategyAnalysisResponse getStrategyAnalysis(Long strategyId, AnalysisOption option1,
                                                        AnalysisOption option2) {
        validateOption(option1, option2);
        validateStrategyExists(strategyId);

        // 동일 옵션 처리
        if (option1.equals(option2)) {
            return getSingleOptionAnalysis(strategyId, option1);
        }
        return dailyAnalysisRepository.findStrategyAnalysisData(strategyId, option1, option2);

    }

    private void validateOption(AnalysisOption option1, AnalysisOption option2) {
        if (option1 == null || option2 == null) {
            throw new BusinessException(ErrorCode.ANALYSIS_OPTION_NOT_FOUND);
        }
    }

    private StrategyAnalysisResponse getSingleOptionAnalysis(Long strategyId, AnalysisOption option) {
        List<AnalysisDataDto> data = dailyAnalysisRepository.findSingleOptionAnalysisData(strategyId, option);

        List<String> dates = data.stream()
                .map(AnalysisDataDto::getDate)
                .toList();

        List<Double> yAxis = data.stream()
                .map(AnalysisDataDto::getYAxis)
                .toList();

        return StrategyAnalysisResponse.builder()
                .dates(dates)
                .data(Map.of(option.name(), yAxis))
                .build();
    }

    /**
     * 일간 분석 데이터 엑셀 다운로드용 조회
     */
    public List<DailyAnalysisResponse> getDailyAnalysisExcelData(Long strategyId) {
        validateStrategyExists(strategyId);
        return dailyAnalysisRepository.findDailyAnalysisForExcel(strategyId);
    }

    /**
     * 월간 분석 데이터 엑셀 다운로드용 조회
     */
    public List<MonthlyAnalysisResponse> getMonthlyAnalysisExcelData(Long strategyId) {
        validateStrategyExists(strategyId);
        return monthlyAnalysisRepository.findByStrategyStrategyId(strategyId)
                .stream()
                .map(MonthlyAnalysisResponse::from)
                .toList();
    }

    /**
     * 전략 계좌 이미지 데이터 조회
     */
    public PageResponseDto<AccountImagesResponseDto> getAccountImages(Long strategyId, Pageable pageable) {
        validateStrategyExists(strategyId);
        Page<AccountImagesResponseDto> result = accountVerificationRepository.findByStrategyId(strategyId, pageable)
                .map(AccountImagesResponseDto::createAccountImages);
        return new PageResponseDto<>(result);
    }

    /**
     * 전략 존재 여부 검증
     */
    private void validateStrategyExists(Long strategyId) {
        if (!strategyRepository.existsByStrategyId(strategyId)) {
            throw new BusinessException(ErrorCode.STRATEGY_NOT_FOUND);
        }
    }

}
