package com.investmetic.domain.notice.repository;


import com.investmetic.domain.notice.model.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
