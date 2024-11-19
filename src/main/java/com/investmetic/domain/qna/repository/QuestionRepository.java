package com.investmetic.domain.qna.repository;

import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {

    Optional<Question> findByStrategyAndQuestionId(Strategy strategy, Long questionId);

    Page<Question> findByUser(User user, Pageable pageable);

    Page<Question> findByStrategy(Strategy strategy, Pageable pageable);
}
