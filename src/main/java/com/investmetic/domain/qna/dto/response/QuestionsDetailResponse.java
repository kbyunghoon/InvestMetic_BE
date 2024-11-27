package com.investmetic.domain.qna.dto.response;

import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionsDetailResponse {
    private final Long questionId;
    private final String title;
    private final String questionContent;
    private final String answerContent;
    private final String strategyName;
    private final String investorImageUrl;
    private final String investorName;
    private final String traderImageUrl;
    private final String traderName;
    private final String state;
    private final String questionCreatedAt;
    private final String answerCreatedAt;

    public static QuestionsDetailResponse from(Question question, Answer answer) {
        return QuestionsDetailResponse.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .questionContent(question.getContent())
                .answerContent(answer != null ? answer.getContent() : null)
                .strategyName(question.getStrategy().getStrategyName())
                .investorImageUrl(question.getUser().getImageUrl())
                .investorName(question.getUser().getNickname())
                .traderImageUrl(question.getStrategy().getUser().getImageUrl())
                .traderName(question.getStrategy().getUser().getNickname())
                .state(question.getQnaState().name())
                .questionCreatedAt(question.getCreatedAt() != null ? question.getCreatedAt().toString() : "N/A")
                .answerCreatedAt(answer != null && answer.getCreatedAt() != null ? answer.getCreatedAt().toString() : null)
                .build();
    }
}
