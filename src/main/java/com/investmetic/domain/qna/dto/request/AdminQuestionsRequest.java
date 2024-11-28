package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminQuestionsRequest extends QuestionRequestDto {
    private final String investorName;
    private final String traderName;
    private final String strategyName;

    @Builder
    public AdminQuestionsRequest(String keyword, SearchCondition searchCondition, StateCondition stateCondition,
                                 String title, String content, String investorName, String traderName,
                                 String strategyName) {
        super(keyword, searchCondition, stateCondition, title, content);
        this.investorName = investorName;
        this.traderName = traderName;
        this.strategyName = strategyName;
    }
}
