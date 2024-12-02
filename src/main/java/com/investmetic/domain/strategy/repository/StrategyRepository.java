package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.dto.response.AdminStrategyResponseDto;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StrategyRepository extends JpaRepository<Strategy, Long>, StrategyRepositoryCustom {

    List<Strategy> findAllByUserUserId(Long userId);

    Boolean existsByStrategyId(Long StrategyId);
}
