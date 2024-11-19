package com.investmetic.domain.qna.repository;

import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepositoryCustom {
    Page<Question> findQuestionsForInvestor(String keyword, QnaState qnaState, User user, Pageable pageable);

    Page<Question> findQuestionsForTrader(String keyword, QnaState qnaState, Strategy strategy, Pageable pageable);

    Page<Question> findQuestionsForAdmin(String keyword, QnaState qnaState, Pageable pageable);
}
