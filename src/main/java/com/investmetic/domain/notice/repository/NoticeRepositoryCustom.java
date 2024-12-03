package com.investmetic.domain.notice.repository;

import com.investmetic.domain.notice.dto.response.NoticeListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {

    Page<NoticeListDto> findNoticelist(String keyword, Pageable pageable);

}
