package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.request.TraderDailyAnalysisRequestDto;
import com.investmetic.domain.strategy.dto.response.DailyAnalysisResponse;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Proceed;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.MonthlyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrategyAnalysisService {
    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final StrategyRepository strategyRepository;
    private final MonthlyAnalysisRepository monthlyAnalysisRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createDailyAnalysis(Long strategyId, List<TraderDailyAnalysisRequestDto> analysisRequests,
                                    Long userId) {
        Set<LocalDate> dateSet = new HashSet<>();
        for (TraderDailyAnalysisRequestDto request : analysisRequests) {
            if (!dateSet.add(request.getDate())) {
                throw new BusinessException(ErrorCode.DUPLICATE_DATE_IN_REQUEST);
            }
        }

        Strategy strategy = findStrategyById(strategyId);

        User user = verifyUser(userId);

        verifyUserPermission(strategy, user);

        for (TraderDailyAnalysisRequestDto analysisRequest : analysisRequests) {
            Optional<DailyAnalysis> existsDailyData = dailyAnalysisRepository.findDailyAnalysisByStrategyAndDate(
                    strategy,
                    analysisRequest.getDate());

            if (existsDailyData.isPresent()) {
                throw new BusinessException(ErrorCode.DAILY_ANALYSIS_ALREADY_EXISTS);
            }

            DailyAnalysis dailyAnalysis = DailyAnalysis.builder()
                    .strategy(strategy)
                    .dailyDate(analysisRequest.getDate())
                    .transaction(analysisRequest.getTransaction())
                    .dailyProfitLoss(analysisRequest.getDailyProfitLoss())
                    .proceed(Proceed.NO)
                    .build();

            dailyAnalysisRepository.save(dailyAnalysis);
        }

        List<DailyAnalysis> dailyAnalyses = dailyAnalysisRepository.findByStrategy(strategy);

        if (dailyAnalyses.size() >= 3) {
            strategy.setIsPublic(IsPublic.PUBLIC);
        }
    }

    @Transactional
    public void modifyDailyAnalysis(Long strategyId, TraderDailyAnalysisRequestDto analysisRequest, Long userId) {
        Strategy strategy = findStrategyById(strategyId);

        User user = verifyUser(userId);

        verifyUserPermission(strategy, user);

        DailyAnalysis existsDailyData = dailyAnalysisRepository.findDailyAnalysisByStrategyAndDate(
                        strategy,
                        analysisRequest.getDate())
                .orElseThrow(() -> new BusinessException(ErrorCode.DAILY_ANALYSIS_NOT_FOUND));

        if (existsDailyData.getProceed() == Proceed.YES) {
            DailyAnalysis dailyAnalysis = DailyAnalysis.builder()
                    .strategy(strategy)
                    .dailyDate(analysisRequest.getDate())
                    .transaction(analysisRequest.getTransaction())
                    .dailyProfitLoss(analysisRequest.getDailyProfitLoss())
                    .proceed(Proceed.NO)
                    .build();

            dailyAnalysisRepository.save(dailyAnalysis);
        } else {
            existsDailyData.modifyDailyAnalysis(analysisRequest.getTransaction(), analysisRequest.getDailyProfitLoss());
        }
    }

    @Transactional
    public void deleteStrategyAllDailyAnalysis(Long strategyId, Long userId) {
        Strategy strategy = findStrategyById(strategyId);

        User user = verifyUser(userId);

        verifyUserPermission(strategy, user);

        strategy.resetStrategyDailyAnalysis();

        dailyAnalysisRepository.deleteAllByStrategy(strategy);
        monthlyAnalysisRepository.deleteAllByStrategy(strategy);
    }

    @Transactional
    public void deleteStrategyDailyAnalysis(Long strategyId, Long analysisId, Long userId) {
        Strategy strategy = findStrategyById(strategyId);

        User user = verifyUser(userId);

        verifyUserPermission(strategy, user);

        DailyAnalysis dailyAnalysis = dailyAnalysisRepository.findByDailyAnalysisId(analysisId)
                .orElseThrow(() -> new BusinessException((ErrorCode.INVALID_TYPE_VALUE)));

        LocalDate dailyDate = dailyAnalysis.getDailyDate();
        Optional<DailyAnalysis> nextDailyAnalysis = dailyAnalysisRepository.findByAfterDate(strategy, dailyDate);
        nextDailyAnalysis.ifPresent(analysis -> analysis.setProceed(Proceed.NO));

        if (nextDailyAnalysis.isEmpty()) {
            strategy.resetStrategyDailyAnalysis();
            monthlyAnalysisRepository.deleteAllByStrategy(strategy);
        }

        dailyAnalysisRepository.deleteByStrategyAndDailyAnalysisId(strategy, analysisId);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<DailyAnalysisResponse> getMyDailyAnalysis(Long strategyId, Pageable pageable) {
        Page<DailyAnalysisResponse> myDailyAnalysis = dailyAnalysisRepository.findMyDailyAnalysis(strategyId, pageable);
        return new PageResponseDto<>(myDailyAnalysis);
    }

    private Strategy findStrategyById(Long strategyId) {
        return strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
    }

    private User verifyUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }

    private void verifyUserPermission(Strategy strategy, User user) {
        if (user.getRole() == Role.TRADER && !strategy.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
