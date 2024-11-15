package com.investmetic.domain.qna.repository;

import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findByStrategyAndQuestionId(Strategy strategy, Long questionId);

//    List<Question> findAllByUser(User user);
//    List<Question> findAllByStrategy(Strategy strategy);

}
