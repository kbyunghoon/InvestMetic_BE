package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.entity.QUser;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.investmetic.domain.user.model.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    /**
     * 회원 정보 제공
     */
    @Override
    public Optional<UserProfileDto> findByEmailUserInfo(String email) {

        QUser user = QUser.user;

        return Optional.ofNullable(queryFactory.from(user)
                .select(Projections.fields(UserProfileDto.class,
                        user.userId, user.userName, user.nickname, user.email, user.imageUrl, user.phone,
                        user.infoAgreement))
                .where(user.email.eq(email))
                .fetchOne()); // 하나의 객체 반환
    }

    /**
     * 관리자 회원 관리 페이지 회원 목록 페이지네이션.
     * */
    @Override
    public Page<UserProfileDto> getAdminUsersPage(UserAdminPageRequestDto pageRequestDto, Pageable pageable){

        QUser user = QUser.user;

        Optional<List<UserProfileDto>> content = Optional.ofNullable(queryFactory
                .select(Projections.fields(UserProfileDto.class,
                        user.userId,
                        user.userName,
                        user.email,
                        user.imageUrl,
                        user.phone,
                        user.infoAgreement,
                        user.role))
                .from(user)
                .where(keywordCondition(pageRequestDto.getCondition(),pageRequestDto.getKeyword()))
                .orderBy(orderByLatest())
                .offset(pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .fetch());

        //null일 경우 throw;
        content.orElseThrow(()->new BusinessException(ErrorCode.USERS_NOT_FOUND));

        // PageableExecutionUtils 이용. count쿼리 최소화.
        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .where(keywordCondition(pageRequestDto.getCondition(),pageRequestDto.getKeyword()))
                .from(user);

        return PageableExecutionUtils.getPage(content.get(), pageable, countQuery::fetchOne);
    }

    private BooleanExpression keywordCondition(String condition , String keyword) {

        //null, "", 빈 문자열이면  null 반환
        if (StringUtils.isBlank(condition)) {
            return null;
        }

        // condition에 따라 조건 컬럼 검색.
        return switch (condition) {
            case "nickname" -> user.nickname.eq(keyword);
            case "email" -> user.email.eq(keyword);
            case "phone" -> user.phone.eq(keyword);
            default -> user.userName.contains(keyword);
        };
    }

    //최신 생성순.
    private OrderSpecifier<?> orderByLatest(){

        //userId 는 시간순으로 정해짐. 인덱스
        return new OrderSpecifier<>(Order.DESC, QUser.user.userId);
    }
}