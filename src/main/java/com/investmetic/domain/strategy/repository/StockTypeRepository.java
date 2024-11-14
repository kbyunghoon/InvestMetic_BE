package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.StockType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTypeRepository extends JpaRepository<StockType, Long> {

}
