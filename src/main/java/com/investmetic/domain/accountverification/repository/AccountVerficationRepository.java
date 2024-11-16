package com.investmetic.domain.accountverification.repository;

import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountVerficationRepository extends JpaRepository<AccountVerification, Long> {
}
