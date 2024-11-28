package com.investmetic.domain.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class QuestionsDetailResponse {
    private final Long questionId; // 문의 ID
    private final String title; // 문의 제목
    private final String questionContent; // 문의 내용
    private final String answerContent; // 답변 내용
    private final String strategyName; // 전략 이름
    private final String investorImageUrl; // 투자자 이미지 URL
    private final String investorName; // 투자자 이름
    private final String traderImageUrl; // 트레이더 이미지 URL
    private final String traderName; // 트레이더 이름
    private final String state; // 문의 상태
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime questionCreatedAt; // 문의 생성일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime answerCreatedAt; // 답변 생성일

    // 정적 팩토리 메서드
    public static QuestionsDetailResponse from(Question question, Answer answer) {
        return new QuestionsDetailResponse(
                question.getQuestionId(),
                question.getTitle(),
                question.getContent(),
                answer != null ? answer.getContent() : null,
                question.getStrategy().getStrategyName(),
                question.getUser().getImageUrl(),
                question.getUser().getNickname(),
                question.getStrategy().getUser().getImageUrl(),
                question.getStrategy().getUser().getNickname(),
                question.getQnaState().name(),
                question.getCreatedAt(),
                answer != null ? answer.getCreatedAt() : null
        );
    }
}
