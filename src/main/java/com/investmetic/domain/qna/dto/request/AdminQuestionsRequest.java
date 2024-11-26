package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminQuestionsRequest {
    private String keyword;
    private String investorName;
    private String traderName;
    private String strategyName;
    private SearchCondition searchCondition;
    private StateCondition stateCondition;


}
