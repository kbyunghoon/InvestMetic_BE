package com.investmetic.domain.notice.repository;

import com.investmetic.domain.notice.model.entity.Notice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {

    List<Notice> findAllByUserUserId(Long userId);
}
