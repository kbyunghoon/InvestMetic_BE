package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import lombok.Builder;
import lombok.Getter;

@Getter
public class InvestorQuestionsRequest {

    private final String keyword;
    private final SearchCondition searchCondition;
    private final StateCondition stateCondition;
    private final String strategyName;
    private final String traderName;

    @Builder
    public InvestorQuestionsRequest(String keyword, SearchCondition searchCondition, StateCondition stateCondition,
                                    String strategyName, String traderName) {
        this.keyword = keyword;
        this.searchCondition = searchCondition;
        this.stateCondition = stateCondition;
        this.strategyName = strategyName;
        this.traderName = traderName;
    }
}
