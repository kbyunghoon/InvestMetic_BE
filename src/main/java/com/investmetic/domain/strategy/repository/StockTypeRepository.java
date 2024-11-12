package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.StockType;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockTypeRepository extends CrudRepository<StockType, Long> {

}
