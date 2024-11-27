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
                .title(question.getTitle() != null ? question.getTitle() : "제목 없음") // 기본값 추가
                .questionContent(question.getContent() != null ? question.getContent() : "내용 없음") // 기본값 추가
                .answerContent(answer != null ? answer.getContent() : "답변 없음") // 기본값 추가
                .strategyName(question.getStrategy() != null && question.getStrategy().getStrategyName() != null
                        ? question.getStrategy().getStrategyName()
                        : "전략 없음") // 전략 이름 기본값
                .investorImageUrl(question.getUser() != null && question.getUser().getImageUrl() != null
                        ? question.getUser().getImageUrl()
                        : "이미지 없음") // 투자자 이미지 기본값
                .investorName(question.getUser() != null && question.getUser().getNickname() != null
                        ? question.getUser().getNickname()
                        : "투자자 이름 없음") // 투자자 이름 기본값
                .traderImageUrl(question.getStrategy() != null && question.getStrategy().getUser() != null
                        && question.getStrategy().getUser().getImageUrl() != null
                        ? question.getStrategy().getUser().getImageUrl()
                        : "이미지 없음") // 트레이더 이미지 기본값
                .traderName(question.getStrategy() != null && question.getStrategy().getUser() != null
                        && question.getStrategy().getUser().getNickname() != null
                        ? question.getStrategy().getUser().getNickname()
                        : "트레이더 이름 없음") // 트레이더 이름 기본값
                .state(question.getQnaState() != null ? question.getQnaState().name() : "상태 없음") // 상태 기본값
                .questionCreatedAt(question.getCreatedAt() != null
                        ? question.getCreatedAt().toString()
                        : "생성일 없음") // 질문 생성일 기본값
                .answerCreatedAt(answer != null && answer.getCreatedAt() != null
                        ? answer.getCreatedAt().toString()
                        : "답변 생성일 없음") // 답변 생성일 기본값
                .build();
    }
}
