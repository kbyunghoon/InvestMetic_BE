package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.StockTypeGroup;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockTypeGroupRepository extends JpaRepository<StockTypeGroup, Long> {

    @Query("SELECT stg.stockType FROM StockTypeGroup stg WHERE stg.strategy = :strategy")
    List<StockType> findStockTypeIdsByStrategy(@Param("strategy") Strategy strategy);

}
