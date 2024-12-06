package com.investmetic.domain.notice.repository;

import static com.investmetic.domain.notice.model.entity.QNotice.notice;
import static com.investmetic.domain.notice.model.entity.QNoticeFile.noticeFile;
import static com.investmetic.domain.user.model.entity.QUser.user;

import com.investmetic.domain.notice.dto.object.QNoticeOwnerDto;
import com.investmetic.domain.notice.dto.response.NoticeDetailResponseDto;
import com.investmetic.domain.notice.dto.response.NoticeFileResponseDto;
import com.investmetic.domain.notice.dto.response.NoticeListDto;
import com.investmetic.domain.notice.dto.response.QNoticeDetailResponseDto;
import com.investmetic.domain.notice.dto.response.QNoticeFileResponseDto;
import com.investmetic.domain.notice.dto.response.QNoticeListDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public NoticeDetailResponseDto findByNoticeId(Long noticeId) {
        List<NoticeFileResponseDto> files = queryFactory
                .select(new QNoticeFileResponseDto(noticeFile.noticeFileId, noticeFile.fileName))
                .from(noticeFile)
                .where(noticeFile.notice.noticeId.eq(noticeId))
                .fetch();

        NoticeDetailResponseDto noticeDetail = queryFactory
                .select(new QNoticeDetailResponseDto(
                        notice.title,
                        notice.content,
                        notice.createdAt
                ))
                .from(notice)
                .where(notice.noticeId.eq(noticeId))
                .fetchOne();

        noticeDetail.updateFiles(files);

        return noticeDetail;
    }

    @Override
    public Page<NoticeListDto> findNoticelist(String keyword, Pageable pageable) {

        // 컨텐츠 조회
        List<NoticeListDto> content = queryFactory
                .select(new QNoticeListDto(
                        notice.noticeId,
                        new QNoticeOwnerDto(
                                notice.user.userId,
                                notice.user.nickname
                        ),
                        notice.title,
                        notice.content,
                        Expressions.stringTemplate("DATE_FORMAT({0}, {1})", notice.createdAt, "%Y.%m.%d").as("createdAt")
                ))
                .from(notice)
                .join(notice.user, user)
                .where(titleKeyword(keyword))
                .orderBy(notice.noticeId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(notice.count())
                .from(notice);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private BooleanExpression titleKeyword(String keyword) {
        if (keyword != null) {
            return notice.title.contains(keyword);
        }
        return null;
    }
}
