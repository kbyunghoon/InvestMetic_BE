package com.investmetic.domain.notice.repository;

import com.investmetic.domain.notice.dto.response.NoticeDetailResponseDto;

public interface NoticeRepositoryCustom {
    NoticeDetailResponseDto findByNoticeId(Long noticeId);
}
