package com.investmetic.domain.qna.repository;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.user.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepositoryCustom {

    Page<Question> searchQuestions(Long userId, String keyword, SearchCondition searchCondition,
                                   StateCondition stateCondition, Role role, Pageable pageable,
                                   String strategyName, String traderName, String investorName);
}
