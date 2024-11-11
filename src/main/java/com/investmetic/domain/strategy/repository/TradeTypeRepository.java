package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.TradeType;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TradeTypeRepository extends CrudRepository<TradeType, Long> {
    List<TradeType> findByActivateStateTrue();
}
