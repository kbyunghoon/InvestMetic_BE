package com.investmetic.domain.qna.dto.response;

import com.investmetic.domain.qna.model.entity.Question;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionsResponse {
    private Long questionId;
    private String title;
    private String qusetionContent;
    private String strategyName;
    private String traderName;
    private String investorName;
    private String stateCondition;
    private String createdAt;

    public static QuestionsResponse from(Question question) {
        return QuestionsResponse.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .qusetionContent(question.getContent())
                .strategyName(question.getStrategy().getStrategyName())
                .traderName(question.getStrategy().getUser().getNickname())
                .investorName(question.getUser().getUserName())
                .stateCondition(question.getQnaState().name())
                .createdAt(question.getCreatedAt().toString())
                .build();
    }
}
