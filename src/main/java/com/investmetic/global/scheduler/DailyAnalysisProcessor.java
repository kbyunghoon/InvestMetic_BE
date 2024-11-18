package com.investmetic.global.scheduler;


import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.global.util.DailyAnalysisCalculator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyAnalysisProcessor implements ItemProcessor<DailyAnalysis, DailyAnalysis> {
    private final DailyAnalysisRepository dailyAnalysisRepository;

    @Override
    public DailyAnalysis process(DailyAnalysis currentAnalysis) {
        // 1. 전날 데이터 가져오기
        Optional<DailyAnalysis> previousAnalysisOpt = dailyAnalysisRepository.findLatestBefore(
                currentAnalysis.getStrategy().getStrategyId(),
                currentAnalysis.getDailyDate()
        );

        if (previousAnalysisOpt.isEmpty()) {
            // 원금 (현재 거래 금액을 가져옴)
            Long principal = currentAnalysis.getTransaction();

            // 잔고 계산 (일간 손익 + 원금)
            Long balance = currentAnalysis.getDailyProfitLoss() + principal;

            // 평가손익 계산 (원금 - 잔고)
            Long valuationProfitLoss = principal - balance;

            // 기준가 및 일간 손익률 계산
            // 기준가(referencePrice) = 잔고 / 원금 * 1000, 원금이 0인 경우 0 반환
            double referencePrice = principal != 0 ? (double) balance / principal * 1000 : 0;
            // 일간 손익률 = (기준가 - 1000) / 1000
            double dailyProfitLossRate = (referencePrice - 1000) / 1000;

            // 최대 일간 이익 및 최대 일간 손실률 계산
            // 최대 일간 이익 (손익이 양수일 경우 해당 값, 그렇지 않으면 0)
            Long maxDailyProfit = Math.max(currentAnalysis.getDailyProfitLoss(), 0);
            // 최대 일간 손실률 (손익률이 음수일 경우 해당 값, 양수일 경우 0)
            double maxDailyLossRate = dailyProfitLossRate < 0 ? dailyProfitLossRate : 0;

            // 총 이익 및 이익일 수 계산
            // 총 이익 = 최대 일간 이익
            Long totalProfit = maxDailyProfit;
            // 이익일 수 (총 이익이 양수면 1, 아니면 0)
            int profitableDays = totalProfit > 0 ? 1 : 0;

            // 평균 이익 계산 (0으로 나누기 방지, 이익일 수가 0인 경우 0)
            Long averageProfit = profitableDays > 0 ? totalProfit / profitableDays : 0L;

            // 총 손실 및 손실일 수 계산
            // 총 손실 (일간 손익이 음수일 경우 해당 값, 그렇지 않으면 0)
            Long totalLoss = currentAnalysis.getDailyProfitLoss() < 0 ? currentAnalysis.getDailyProfitLoss() : 0;
            // 손실일 수 (총 손실이 음수면 1, 그렇지 않으면 0)
            int lossDays = totalLoss < 0 ? 1 : 0;

            // 평균 손실 계산 (0으로 나누기 방지, 손실일 수가 0인 경우 0)
            Long averageLoss = lossDays > 0 ? totalLoss / lossDays : 0L;

            // 누적 손익 및 누적 손익률 계산
            // 누적 손익 (현재 일간 손익)
            Long cumulativeProfitLoss = currentAnalysis.getDailyProfitLoss();
            // 누적 손익률 = (기준가 / 1000) - 1
            Double cumulativeProfitLossRate = (referencePrice / 1000) - 1;

            // 최대 누적 손익 및 최대 누적 손익률 계산
            // 최대 누적 손익 (현재까지의 최대 손익)
            Long maxCumulativeProfitLoss = Math.max(cumulativeProfitLoss, 0);
            // 최대 누적 손익률 (기준가를 기준으로 계산)
            Double maxCumulativeProfitLossRate = (referencePrice / 1000) - 1;

            // 평균 손익 및 평균 손익률 계산
            Long averageProfitLoss = cumulativeProfitLoss;
            // 평균 손익률 (누적 손익률이 양수인 경우 해당 값)
            Double averageProfitLossRatio = Math.max(cumulativeProfitLossRate, 0);

            // 고점 및 고점 비율 계산
            Long peak = Math.max(cumulativeProfitLoss, 0);
            Double peakRatio = Math.max(cumulativeProfitLossRate, 0);

            // 고점 이후 경과 일수 (초기값 0)
            int daysSincePeak = 0;

            // 현재 자본인하 계산 (누적 손익이 양수인 경우 누적 손익 - 최대 누적 손익)
            Long currentDrawdown = cumulativeProfitLoss > 0 ? cumulativeProfitLoss - maxCumulativeProfitLoss : 0;
            // 현재 자본인하율 (초기값 0.0)
            Double currentDrawdownRate = 0.0;

            // 최대 자본인하 및 자본인하율 계산
            // 최대 자본인하 (현재 자본인하 중 최솟값)
            Long maxDrawdown = Math.min(currentDrawdown, 0);
            Double maxDrawdownRate = 0.0;

            // 승률 계산 (이익일 수를 퍼센트로 환산)
            Double winRate = (double) (profitableDays) * 100;

            // Profit Factor 계산 (총 손실이 음수인 경우 총 이익 / 총 손실 절대값)
            Double profitFactor = totalLoss < 0 ? (double) totalProfit / Math.abs(totalLoss) : 0;

            // ROA (Return on Assets) 계산 (누적 손익을 최대 자본인하로 나눈 값의 음수)
            Double roa = maxDrawdown != 0 ? (double) cumulativeProfitLoss / maxDrawdown * -1 : 0;

            // 변동계수 (초기값 0.0)
            Double coefficientOfVariation = 0.0;

            // 샤프 비율 (초기값 0.0)
            Double sharpRatio = 0.0;

            // kp ratio, sm score

            return DailyAnalysis.builder()
                    .dailyAnalysisId(currentAnalysis.getDailyAnalysisId())
                    .strategy(currentAnalysis.getStrategy())
                    .dailyDate(currentAnalysis.getDailyDate())
                    .transaction(currentAnalysis.getTransaction())
                    .dailyProfitLoss(currentAnalysis.getDailyProfitLoss())
                    .principal(principal)
                    .balance(balance)
                    .valuationProfitLoss(valuationProfitLoss)
                    .referencePrice(referencePrice)
                    .cumulativeTransactionAmount(0L)
                    .deposit(0L)
                    .cumulativeDeposit(0L)
                    .withdrawal(0L)
                    .cumulativeWithdrawal(0L)
                    .dailyProfitLossRate(dailyProfitLossRate)
                    .maxDailyProfit(maxDailyProfit)
                    .maxDailyProfitRate(dailyProfitLossRate)
                    .maxDailyLossRate(maxDailyLossRate)
                    .totalProfit(totalProfit)
                    .profitableDays(profitableDays)
                    .averageProfit(averageProfit)
                    .totalLoss(totalLoss)
                    .lossDays(lossDays)
                    .averageLoss(averageLoss)
                    .cumulativeProfitLoss(cumulativeProfitLoss)
                    .cumulativeProfitLossRate(cumulativeProfitLossRate)
                    .maxCumulativeProfitLoss(maxCumulativeProfitLoss)
                    .maxCumulativeProfitLossRate(maxCumulativeProfitLossRate)
                    .averageProfitLoss(averageProfitLoss)
                    .averageProfitLossRatio(averageProfitLossRatio)
                    .peak(peak)
                    .peakRatio(peakRatio)
                    .daysSincePeak(daysSincePeak)
                    .currentDrawdown(currentDrawdown)
                    .currentDrawdownRate(currentDrawdownRate)
                    .maxDrawdown(maxDrawdown)
                    .maxDrawdownRate(maxDrawdownRate)
                    .winRate(winRate)
                    .profitFactor(profitFactor)
                    .roa(roa)
                    .coefficientOfVariation(coefficientOfVariation)
                    .sharpRatio(sharpRatio)
                    .build();
        }

        // 2. 이전 데이터 기반 계산
        long previousBalance = previousAnalysisOpt.map(DailyAnalysis::getBalance).orElse(0L);
        Double previousReferencePrice = previousAnalysisOpt.map(DailyAnalysis::getReferencePrice).orElse(
                1000.0);
        long previousCumulativeProfitLoss = previousAnalysisOpt.map(DailyAnalysis::getCumulativeProfitLoss).orElse(0L);

        // 3. 누적 손익 계산
        long newCumulativeProfitLoss = previousCumulativeProfitLoss + currentAnalysis.getDailyProfitLoss();

        // 4. 입출금 계산
        long deposit = DailyAnalysisCalculator.calculateDeposit(currentAnalysis);
        long withdrawal = DailyAnalysisCalculator.calculateWithdrawal(currentAnalysis);

        // 5. 원금, 잔고 및 평가손익 계산
        // 원금
        long updatedPrincipal = DailyAnalysisCalculator.updatePrincipal(currentAnalysis, previousAnalysisOpt);
        long newBalance = DailyAnalysisCalculator.calculateBalance(previousBalance, currentAnalysis.getTransaction(),
                currentAnalysis.getDailyProfitLoss());
        long valuationProfitLoss = DailyAnalysisCalculator.calculateValuationProfitLoss(updatedPrincipal, newBalance);

        // 6. 기준가 및 일간 손익률 계산
        long referencePrice = DailyAnalysisCalculator.calculateReferencePrice(newBalance, updatedPrincipal);
        double dailyProfitLossRate = DailyAnalysisCalculator.calculateDailyProfitLossRate(referencePrice,
                previousReferencePrice);

        // 7. 해당 전략 내의 모든 분석 데이터 가져오기
        List<DailyAnalysis> allAnalyses = dailyAnalysisRepository.findAllByStrategy(
                currentAnalysis.getStrategy().getStrategyId());

        System.out.println("allAnalyses.size() : " + allAnalyses.size());

        // 8. 누적 입출금 및 최대 이익/손실 계산
        long cumulativeDeposit = DailyAnalysisCalculator.calculateCumulativeDeposit(allAnalyses);
        long cumulativeWithdrawal = DailyAnalysisCalculator.calculateCumulativeWithdrawal(allAnalyses);
        long maxDailyProfit = DailyAnalysisCalculator.calculateMaxDailyProfit(allAnalyses);
        double maxDailyProfitRate = DailyAnalysisCalculator.calculateMaxDailyProfitRate(allAnalyses);
        double maxDailyLossRate = DailyAnalysisCalculator.calculateMaxDailyLossRate(allAnalyses);

        // 9. 새로운 DailyAnalysis 객체 생성 및 반환
        return DailyAnalysis.builder()
                .strategy(currentAnalysis.getStrategy())
                .dailyAnalysisId(currentAnalysis.getDailyAnalysisId())
                .dailyDate(currentAnalysis.getDailyDate())
                .transaction(currentAnalysis.getTransaction())
                .deposit(deposit)
                .withdrawal(withdrawal)
                .cumulativeProfitLoss(newCumulativeProfitLoss)
                .cumulativeProfitLossRate(
                        DailyAnalysisCalculator.calculateCumulativeProfitLossRate(newCumulativeProfitLoss,
                                updatedPrincipal))
                .principal(updatedPrincipal)
                .balance(newBalance)
                .valuationProfitLoss(valuationProfitLoss)
                .referencePrice(referencePrice)
                .dailyProfitLossRate(dailyProfitLossRate)
                .cumulativeDeposit(cumulativeDeposit)
                .cumulativeWithdrawal(cumulativeWithdrawal)
                .maxDailyProfit(maxDailyProfit)
                .maxDailyProfitRate(maxDailyProfitRate)
                .maxDailyLossRate(maxDailyLossRate)
                .build();
    }
}
