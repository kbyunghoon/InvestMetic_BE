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
                .strategyName(question.getStrategy() != null ? question.getStrategy().getStrategyName() : "N/A")
                .traderImageUrl(question.getStrategy() != null && question.getStrategy().getUser() != null
                        ? question.getStrategy().getUser().getImageUrl()
                        : "N/A")
                .investorImageUrl(question.getUser() != null ? question.getUser().getImageUrl() : "N/A")
                .traderName(question.getStrategy() != null && question.getStrategy().getUser() != null
                        ? question.getStrategy().getUser().getNickname()
                        : "N/A")
                .investorName(question.getUser() != null ? question.getUser().getNickname() : "N/A")
                .stateCondition(question.getQnaState() != null ? question.getQnaState().name() : "UNKNOWN")
                .createdAt(question.getCreatedAt() != null ? question.getCreatedAt().toString() : "N/A")
                .build();
    }
}
