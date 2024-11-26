package com.investmetic.domain.qna.repository;

import com.investmetic.domain.qna.model.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

}
