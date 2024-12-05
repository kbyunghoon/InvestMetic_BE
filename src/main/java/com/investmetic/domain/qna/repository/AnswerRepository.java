package com.investmetic.domain.qna.repository;

import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByQuestion(Question question);

    @Modifying
    @Query("DELETE FROM Answer a WHERE a.question IN :questions")
    void deleteByQuestions(@Param("questions") List<Question> questions);
}
