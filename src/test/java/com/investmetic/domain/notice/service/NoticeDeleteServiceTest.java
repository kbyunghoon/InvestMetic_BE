package com.investmetic.domain.notice.service;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.notice.model.entity.NoticeFile;
import com.investmetic.domain.notice.repository.NoticeFileRepository;
import com.investmetic.domain.notice.repository.NoticeRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.S3FileService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("공지사항 삭제")
@ExtendWith(MockitoExtension.class)
public class NoticeDeleteServiceTest {

    @Mock
    private S3FileService s3FileService;

    @InjectMocks
    private NoticeService noticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NoticeFileRepository noticeFileRepository;

    private User admin;

    private Notice notice;

    private NoticeFile noticeFile;

    private User user;


    @BeforeEach
    void setUp() {
        admin = User.builder()
                .userId(1L)
                .email("TTEESSTT@EXAMPLE.com")
                .phone("01032356489")
                .nickname("ADMINADMIN")
                .role(Role.SUPER_ADMIN)
                .nickname("ADMINNICKNAME")
                .build();

        notice = Notice.builder()
                .noticeId(1L)
                .title("test")
                .content("testest")
                .user(admin)
                .build();

        noticeFile = NoticeFile.builder()
                .notice(notice)
                .noticeFileId(1L)
                .fileName("s")
                .fileUrl(".com/asdf/asdf/aa.jpg")
                .build();

        user = User.builder()
                .userId(2L)
                .email("werafgerah@EXAMPLE.com")
                .phone("010614513465")
                .nickname("notvalid")
                .role(Role.INVESTOR)
                .nickname("notvalid")
                .build();
    }

    @Test
    @DisplayName("공지사항을 삭제합니다. 정상")
    void deleteNotice1(){
        // given
        List<NoticeFile> list = new ArrayList<>(List.of(noticeFile));

        when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(noticeFileRepository.findAllByNoticeNoticeId(anyLong())).thenReturn(list);

        // when
        noticeService.deleteNotice(notice.getNoticeId(), admin.getUserId());

        // then
        verify(noticeRepository).deleteById(notice.getNoticeId());
        verify(s3FileService).deleteByKeyList(anyList());
    }

    @Test
    @DisplayName("권한 없는경우.")
    void deleteNotice2(){
        // given
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // when
        assertThatThrownBy(()->noticeService.deleteNotice(notice.getNoticeId(), user.getUserId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.AUTHORIZATION_DENIED.getMessage());
    }

}
