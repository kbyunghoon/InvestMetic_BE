package com.investmetic.domain.notice.service;

import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.notice.repository.NoticeRepository;
import com.investmetic.domain.user.model.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

@SpringBootTest
@Transactional
public class NoticeServiceTest {
    @Autowired
    private NoticeService noticeService;

}
