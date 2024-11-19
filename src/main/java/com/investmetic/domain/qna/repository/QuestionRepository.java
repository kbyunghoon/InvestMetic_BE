package com.investmetic.domain.qna.repository;

import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {

    Optional<Question> findByStrategyAndQuestionId(Strategy strategy, Long questionId);


}
