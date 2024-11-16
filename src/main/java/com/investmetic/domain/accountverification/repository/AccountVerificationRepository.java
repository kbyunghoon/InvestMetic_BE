package com.investmetic.domain.accountverification.repository;

import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountVerificationRepository extends JpaRepository<AccountVerification, Long> {
    List<AccountVerification> findByStrategy_StrategyId(Long strategyId);
}
