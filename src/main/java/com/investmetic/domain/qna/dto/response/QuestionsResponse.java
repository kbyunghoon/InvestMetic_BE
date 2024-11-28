package com.investmetic.domain.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.investmetic.domain.qna.model.entity.Question;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class QuestionsResponse {
    private final Long questionId; // 문의 ID
    private final String title; // 문의 제목
    private final String questionContent; // 문의 내용
    private final String strategyName; // 전략 이름
    private final String traderImageUrl; // 트레이더 이미지 URL
    private final String investorImageUrl; // 투자자 이미지 URL
    private final String traderName; // 트레이더 이름
    private final String investorName; // 투자자 이름
    private final String stateCondition; // 문의 상태
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt; // 문의 생성일

    public static QuestionsResponse from(Question question) {
        return new QuestionsResponse(
                question.getQuestionId(),
                question.getTitle(),
                question.getContent(),
                question.getStrategy() != null ? question.getStrategy().getStrategyName() : null,
                question.getStrategy() != null && question.getStrategy().getUser() != null
                        ? question.getStrategy().getUser().getImageUrl()
                        : null,
                question.getUser() != null ? question.getUser().getImageUrl() : null,
                question.getStrategy() != null && question.getStrategy().getUser() != null
                        ? question.getStrategy().getUser().getNickname()
                        : null,
                question.getUser() != null ? question.getUser().getNickname() : null,
                question.getQnaState() != null ? question.getQnaState().name() : null,
                question.getCreatedAt()
        );
    }
}
