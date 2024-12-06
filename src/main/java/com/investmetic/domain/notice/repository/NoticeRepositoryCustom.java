package com.investmetic.domain.notice.repository;

import com.investmetic.domain.notice.dto.response.NoticeDetailResponseDto;
import com.investmetic.domain.notice.dto.response.NoticeListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    NoticeDetailResponseDto findByNoticeId(Long noticeId);

    Page<NoticeListDto> findNoticelist(String keyword, Pageable pageable);

}
