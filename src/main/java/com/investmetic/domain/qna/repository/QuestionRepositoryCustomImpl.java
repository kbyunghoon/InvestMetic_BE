package com.investmetic.domain.qna.repository;

import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.model.entity.QQuestion;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public QuestionRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Question> findQuestionsForInvestor(String keyword, QnaState qnaState, User user, Pageable pageable) {
        QQuestion question = QQuestion.question;

        JPQLQuery<Question> query = queryFactory.selectFrom(question)
                .where(
                        question.user.eq(user),
                        keywordContains(keyword, question),
                        stateEquals(qnaState, question)
                );

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    @Override
    public Page<Question> findQuestionsForTrader(String keyword, QnaState qnaState, Strategy strategy, Pageable pageable) {
        QQuestion question = QQuestion.question;

        JPQLQuery<Question> query = queryFactory.selectFrom(question)
                .where(
                        question.strategy.eq(strategy),
                        keywordContains(keyword, question),
                        stateEquals(qnaState, question)
                );

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    @Override
    public Page<Question> findQuestionsForAdmin(String keyword, QnaState qnaState, Pageable pageable) {
        QQuestion question = QQuestion.question;

        JPQLQuery<Question> query = queryFactory.selectFrom(question)
                .where(
                        keywordContains(keyword, question),
                        stateEquals(qnaState, question)
                );

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    private BooleanExpression keywordContains(String keyword, QQuestion question) {
        if (keyword == null || keyword.isBlank()) return null;
        return question.title.containsIgnoreCase(keyword)
                .or(question.strategy.strategyName.containsIgnoreCase(keyword))
                .or(question.content.containsIgnoreCase(keyword));
    }

    private BooleanExpression stateEquals(QnaState qnaState, QQuestion question) {
        return qnaState != null ? question.qnaState.eq(qnaState) : null;
    }
}
