package com.investmetic.domain.accountverification.repository;

import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountVerificationRepository extends JpaRepository<AccountVerification, Long> {
    @Query("SELECT acv FROM AccountVerification acv WHERE acv.strategy.strategyId = :strategyId")
    Page<AccountVerification> findByStrategyId(Long strategyId, Pageable pageable);
}
