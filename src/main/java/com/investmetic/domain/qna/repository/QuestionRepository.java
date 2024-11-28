package com.investmetic.domain.qna.repository;

import static com.investmetic.domain.qna.model.entity.QQuestion.question;
import static com.investmetic.domain.strategy.model.entity.QStrategy.strategy;
import static com.investmetic.domain.user.model.entity.QUser.user;

import com.investmetic.domain.qna.model.entity.Question;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 사용자 정의 메서드 추가
    default Page<Question> searchByConditions(List<BooleanExpression> conditions, Pageable pageable,
                                              JPAQueryFactory queryFactory) {
        // 데이터 조회 쿼리
        List<Question> content = queryFactory.selectFrom(question)
                .leftJoin(question.strategy, strategy).fetchJoin()
                .leftJoin(question.user, user).fetchJoin()
                .where(conditions.toArray(new BooleanExpression[0]))
                .orderBy(question.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        long total = queryFactory.select(question.count())
                .from(question)
                .where(conditions.toArray(new BooleanExpression[0]))
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }
}
