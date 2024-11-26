package com.investmetic.domain.qna.repository;

import static com.investmetic.domain.qna.model.entity.QQuestion.question;
import static com.investmetic.domain.strategy.model.entity.QStrategy.strategy;
import static com.investmetic.domain.user.model.entity.QUser.user;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.user.model.Role;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Question> searchQuestions(Long userId, String keyword, SearchCondition searchCondition,
                                          StateCondition stateCondition, Role role, Pageable pageable,
                                          String strategyName, String traderName, String investorName) {

        List<BooleanExpression> conditions = new ArrayList<>();

        // 역할에 따른 필터링 조건 추가
        if (role == Role.INVESTOR && userId != null) {
            conditions.add(question.user.userId.eq(userId));
        } else if (role == Role.TRADER && userId != null) {
            conditions.add(question.strategy.user.userId.eq(userId));
        }
        // Admin은 모든 문의를 조회하므로 추가 필터링 없음

        // 검색 조건 추가
        if (StringUtils.isNotBlank(keyword) && searchCondition != null) {
            switch (searchCondition) {
                case TITLE:
                    conditions.add(question.title.containsIgnoreCase(keyword));
                    break;
                case CONTENT:
                    conditions.add(question.content.containsIgnoreCase(keyword));
                    break;
                case TITLE_OR_CONTENT:
                    conditions.add(question.title.containsIgnoreCase(keyword)
                            .or(question.content.containsIgnoreCase(keyword)));
                    break;
                case TRADER_NAME:
                    conditions.add(user.nickname.containsIgnoreCase(keyword));
                    break;
                case INVESTOR_NAME:
                    conditions.add(question.user.nickname.containsIgnoreCase(keyword));
                    break;
                case STRATEGY_NAME:
                    conditions.add(strategy.strategyName.containsIgnoreCase(keyword));
                    break;
                default:
                    break;
            }
        }

        if (stateCondition != null) {
            switch (stateCondition) {
                case WAITING -> {
                    conditions.add(question.qnaState.eq(QnaState.WAITING));
                }
                case COMPLETED -> {
                    conditions.add(question.qnaState.eq(QnaState.COMPLETED));
                }
                case ALL -> {
                    // 'ALL'인 경우 특별한 필터링 없이 모든 상태 포함
                }
                default -> {
                    throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE);
                }
            }
        }

        // 쿼리 생성
        JPAQuery<Question> query = queryFactory.selectFrom(question)
                .leftJoin(question.strategy, strategy).fetchJoin()
                .leftJoin(question.user, user).fetchJoin()
                .where(conditions.toArray(new Predicate[0]))
                .orderBy(question.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Question> content = query.fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory.select(question.count())
                .from(question)
                .leftJoin(question.strategy, strategy)
                .leftJoin(question.user, user)
                .where(conditions.toArray(new Predicate[0]));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
