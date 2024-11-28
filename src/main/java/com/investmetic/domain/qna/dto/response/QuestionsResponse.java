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
                getOrDefault(question.getTitle(), "제목 없음"),
                getOrDefault(question.getContent(), "내용 없음"),
                question.getStrategy() != null ? getOrDefault(question.getStrategy().getStrategyName(), "정보 없음") : "정보 없음",
                question.getStrategy() != null && question.getStrategy().getUser() != null
                        ? getOrDefault(question.getStrategy().getUser().getImageUrl(), "이미지 없음")
                        : "이미지 없음",
                question.getUser() != null ? getOrDefault(question.getUser().getImageUrl(), "이미지 없음") : "이미지 없음",
                question.getStrategy() != null && question.getStrategy().getUser() != null
                        ? getOrDefault(question.getStrategy().getUser().getNickname(), "이름 없음")
                        : "이름 없음",
                question.getUser() != null ? getOrDefault(question.getUser().getNickname(), "이름 없음") : "이름 없음",
                question.getQnaState() != null ? question.getQnaState().name() : "상태 없음",
                question.getCreatedAt()
        );
    }

    private static String getOrDefault(String value, String defaultValue) {
        return value != null && !value.isBlank() ? value : defaultValue;
    }
}
