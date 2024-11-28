package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import lombok.Builder;
import lombok.Getter;

@Getter
public class InvestorQuestionsRequest extends QuestionRequestDto {
    private final String strategyName;
    private final String traderName;

    @Builder
    public InvestorQuestionsRequest(String keyword, SearchCondition searchCondition, StateCondition stateCondition,
                                    String title, String content, String strategyName, String traderName) {
        super(keyword, searchCondition, stateCondition, title, content);
        this.strategyName = strategyName;
        this.traderName = traderName;
    }
}
