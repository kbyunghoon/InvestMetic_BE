package com.investmetic.domain.notice.repository;

import com.investmetic.domain.notice.model.entity.NoticeFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
}
