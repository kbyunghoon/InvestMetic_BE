package com.investmetic.global.util;

import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import java.util.List;
import java.util.Optional;

public class DailyAnalysisCalculator {


    /**
     * 거래일수 = 해당 전략의 n번째 분석
     */
    public static int calculateTradingDays(long strategyId, List<DailyAnalysis> analyses) {
        return analyses.size();
    }

    /**
     * 잔고 = (바로 이전 데이터 잔고) + (입출금) + (일손익)
     */
    public static long calculateBalance(long previousBalance, long transaction, long dailyProfitLoss) {
        return previousBalance + transaction + dailyProfitLoss;
    }

    /**
     * 평가손익 = (잔고) - (원금)
     */
    public static long calculateValuationProfitLoss(long principal, long balance) {
        return balance - principal;
    }

    /**
     * 기준가 = (잔고) / (원금) * 1000
     */
    public static long calculateReferencePrice(long balance, long principal) {
        if (principal == 0) {
            return 0;
        }
        return (balance * 1000) / principal;
    }

    /**
     * 누적 입출금 = 해당 전략 처음 날짜부터 현재 날짜까지의 입출금 합
     */
    public static long calculateCumulativeTransaction(List<DailyAnalysis> analyses) {
        return analyses.stream()
                .mapToLong(DailyAnalysis::getTransaction)
                .sum();
    }

    /**
     * 입금 = 입출금이 0보다 많다면 그 값, 그렇지 않으면 0
     */
    public static long calculateDeposit(DailyAnalysis analysis) {
        long transaction = analysis.getTransaction() != null ? analysis.getTransaction() : 0;
        return Math.max(transaction, 0);
    }

    /**
     * 누적 입금 = 해당 전략 처음 날짜부터 현재 날짜까지의 입금 합
     */
    public static long calculateCumulativeDeposit(List<DailyAnalysis> analyses) {
        return analyses.stream()
                .mapToLong(DailyAnalysisCalculator::calculateDeposit)
                .sum();
    }

    /**
     * 출금 = 입출금이 0보다 적다면 그 값, 그렇지 않으면 0
     */
    public static long calculateWithdrawal(DailyAnalysis analysis) {
        long transaction = analysis.getTransaction() != null ? analysis.getTransaction() : 0;
        return transaction < 0 ? transaction : 0;
    }

    /**
     * 누적 출금 = 해당 전략 처음 날짜부터 현재 날짜까지의 출금 합
     */
    public static long calculateCumulativeWithdrawal(List<DailyAnalysis> analyses) {
        return analyses.stream()
                .mapToLong(DailyAnalysisCalculator::calculateWithdrawal)
                .sum();
    }

    /**
     * 일간 손익률 = (기준가 - 바로 이전 데이터 기준가) / 바로 이전 데이터 기준가
     */
    public static double calculateDailyProfitLossRate(long currentReferencePrice, Double previousReferencePrice) {
        if (previousReferencePrice == 0) {
            return 0;
        }
        return (currentReferencePrice - previousReferencePrice) / previousReferencePrice * 100;
    }

    /**
     * 누적 손익률 = 누적 손익 / 원금 * 100
     */
    public static double calculateCumulativeProfitLossRate(long cumulativeProfitLoss, long principal) {
        if (principal == 0) {
            return 0;
        }
        return (double) cumulativeProfitLoss / principal * 100;
    }

    /**
     * 원금 계산 = (바로 이전 데이터 원금) + (입출금 / (바로 이전 데이터 잔고 / 바로 이전 데이터 원금))
     */
    public static long updatePrincipal(DailyAnalysis currentAnalysis, Optional<DailyAnalysis> previousAnalysisOpt) {
        long previousPrincipal = previousAnalysisOpt.map(DailyAnalysis::getPrincipal).orElse(0L);
        long previousBalance = previousAnalysisOpt.map(DailyAnalysis::getBalance).orElse(1L); // 0으로 나누기 방지

        long transaction = currentAnalysis.getTransaction() != null ? currentAnalysis.getTransaction() : 0;

        if (previousBalance != 0 && previousPrincipal != 0) {
            return (long) (previousPrincipal + (transaction / (previousBalance / (double) previousPrincipal)));
        } else {
            // 예외 처리를 위해 기본값을 반환하거나, 필요 시 예외를 발생시킬 수 있습니다.
            return previousPrincipal;
        }

    }

    /**
     * 최대 일간 이익
     */
    public static long calculateMaxDailyProfit(List<DailyAnalysis> analyses) {
        return analyses.stream()
                .mapToLong(DailyAnalysis::getDailyProfitLoss)
                .max()
                .orElse(0);
    }

    /**
     * 최대 일간 이익률
     */
    public static double calculateMaxDailyProfitRate(List<DailyAnalysis> analyses) {
        // null 값 필터링 추가
        return analyses.stream()
                .filter(analysis -> analysis != null && analysis.getDailyProfitLossRate() != null)
                .mapToDouble(DailyAnalysis::getDailyProfitLossRate)
                .max()
                .orElse(0.0); // 데이터가 없을 경우 기본값 0.0 반환
    }


    /**
     * 최대 일간 손실률
     */
    public static double calculateMaxDailyLossRate(List<DailyAnalysis> analyses) {
        return analyses.stream()
                .filter(analysis -> analysis != null && analysis.getDailyProfitLossRate() != null)
                .mapToDouble(DailyAnalysis::getDailyProfitLossRate)
                .min()
                .orElse(0.0); // 데이터가 없거나 모든 값이 null일 경우 기본값 0.0 반환
    }

}
