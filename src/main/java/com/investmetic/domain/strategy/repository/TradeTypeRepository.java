package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.TradeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeTypeRepository extends JpaRepository<TradeType, Long> {
    Page<TradeType> findByActivateState(Boolean activateState, Pageable pageable);
    List<TradeType> findByActivateStateTrue();
    Optional<TradeType> findByTradeTypeId(Long tradeTypeId);
}
