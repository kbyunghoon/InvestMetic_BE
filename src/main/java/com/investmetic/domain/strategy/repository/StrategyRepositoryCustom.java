package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.dto.response.StrategyDetailResponse;

public interface StrategyRepositoryCustom {
    StrategyDetailResponse findStrategyDetail(Long strategyId);
}
