package com.investmetic.domain.notice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.notice.dto.request.NoticeRegisterDto;
import com.investmetic.domain.notice.dto.response.NoticeListDto;
import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.notice.repository.NoticeRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class NoticeListTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User admin = User.builder()
                .email("TTEESSTT@EXAMPLE.com")
                .phone("01032356489")
                .nickname("ADMINADMIN")
                .role(Role.SUPER_ADMIN)
                .nickname("ADMINNICKNAME")
                .build();

        admin = userRepository.save(admin);

        List<Notice> notices = new ArrayList<>();

        for(int i = 1; i<50 ;i++ ){
            Notice notice = NoticeRegisterDto.builder()
                    .content("content"+i)
                    .title("title"+i)
                    .build()
                    .toEntity(admin);
            notices.add(notice);
        }

        noticeRepository.saveAll(notices);
    }

    @Test
    @DisplayName("공지사항 목록 조회")
    void NoticeListTest1() {
        // given
        int i = 0;
        Pageable pageable = PageRequest.of(i, 9);

        Long bigger = Long.MAX_VALUE;

        //when
        PageResponseDto<NoticeListDto> noticeList = noticeService.getUserNoticeList(pageable);

        //then
        while (true) {
            for (NoticeListDto notice : noticeList.getContent()) {

                // 새로운 순.(ID 정렬)
                assertThat(notice.getNoticeId()).isLessThanOrEqualTo(bigger);
                bigger = notice.getNoticeId();

                ++i;
                pageable = PageRequest.of(i, 9);

                // 페이지 올려서 한번더 요청.
                noticeList = noticeService.getUserNoticeList(pageable);
            }

            if (noticeList.getContent().isEmpty()) {
                break;
            }
        }
    }

    @Test
    @DisplayName("관리자 페이지 공지사항 목록 조회")
    void NoticeListTest2() {
        // given
        int i = 0;
        Pageable pageable = PageRequest.of(i, 9);
        String keyword = "3";

        Long bigger = Long.MAX_VALUE;

        //when
        PageResponseDto<NoticeListDto> noticeList = noticeService.getAdminNoticeList(keyword, pageable);

        //then
        while (true) {
            for (NoticeListDto notice : noticeList.getContent()) {

                // 새로운 순.(ID 정렬)
                assertThat(notice.getNoticeId()).isLessThanOrEqualTo(bigger);

                // 공지사항 제목 키워드 검사.
                assertThat(notice.getTitle()).contains(keyword);

                bigger = notice.getNoticeId();

                ++i;
                pageable = PageRequest.of(i, 9);

                // 페이지 올려서 한번더 요청.
                noticeList = noticeService.getUserNoticeList(pageable);
            }

            if (noticeList.getContent().isEmpty()) {
                break;
            }
        }
    }


}
