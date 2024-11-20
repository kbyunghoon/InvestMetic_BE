package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.StockType;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockTypeRepository extends JpaRepository<StockType, Long> {
    Page<StockType> findByActivateState(Boolean activateState, Pageable pageable);

    Optional<StockType> findByStockTypeId(Long stockTypeId);
}
