package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.model.entity.UserHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {


    //메서드명 주의. user의 userid로 찾기.
    List<UserHistory> findByUserUserId(Long id);
}
