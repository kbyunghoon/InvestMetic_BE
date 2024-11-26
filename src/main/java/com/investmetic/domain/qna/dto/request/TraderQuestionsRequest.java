package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraderQuestionsRequest {
    private String keyword;
    private String investorName;
    private String strategyName;
    private SearchCondition searchCondition;
    private StateCondition stateCondition;

}