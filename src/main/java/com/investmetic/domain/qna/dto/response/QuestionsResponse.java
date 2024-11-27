package com.investmetic.domain.qna.dto.response;

import com.investmetic.domain.qna.model.entity.Question;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionsResponse {
    private Long questionId;
    private String title;
    private String questionContent;
    private String strategyName;
    private String traderImageUrl;
    private String investorImageUrl;
    private String traderName;
    private String investorName;
    private String stateCondition;
    private String createdAt;

    public static QuestionsResponse from(Question question) {
        return QuestionsResponse.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .questionContent(question.getContent())
                .strategyName(question.getStrategy().getStrategyName())
                .traderImageUrl(question.getStrategy().getUser().getImageUrl())
                .investorImageUrl(question.getUser().getImageUrl())
                .traderName(question.getStrategy().getUser().getNickname())
                .investorName(question.getUser().getNickname())
                .stateCondition(question.getQnaState().name())
                .createdAt(question.getCreatedAt() != null ? question.getCreatedAt().toString() : "N/A")
                .build();
    }
}
