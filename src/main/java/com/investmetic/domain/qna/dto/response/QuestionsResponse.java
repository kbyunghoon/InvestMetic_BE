package com.investmetic.domain.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.investmetic.domain.qna.dto.BaseQnAInfoDto;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.entity.User;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionsResponse {
    private Long questionId; // 문의 ID
    private String title; // 문의 제목
    private String questionContent; // 문의 내용
    private String strategyName; // 전략 이름
    private QnaState stateCondition; // 문의 상태
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 문의 생성일
    private BaseQnAInfoDto investor;
    private BaseQnAInfoDto trader;

    public static QuestionsResponse forTrader(Question question) {
        User investor = question.getUser();

        return QuestionsResponse.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .questionContent(question.getContent())
                .strategyName(question.getStrategy().getStrategyName())
                .stateCondition(question.getQnaState())
                .createdAt(question.getCreatedAt())
                .investor(BaseQnAInfoDto.builder()
                        .id(investor.getUserId())
                        .userName(investor.getUserName())
                        .profileImageUrl(investor.getImageUrl())
                        .build())
                .build();
    }

    public static QuestionsResponse forInvestor(Question question, User trader) {
        return QuestionsResponse.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .questionContent(question.getContent())
                .strategyName(question.getStrategy().getStrategyName())
                .stateCondition(question.getQnaState())
                .createdAt(question.getCreatedAt())
                .trader(BaseQnAInfoDto.builder()
                        .id(trader.getUserId())
                        .userName(trader.getUserName())
                        .profileImageUrl(trader.getImageUrl())
                        .build())
                .build();
    }

    public static QuestionsResponse forAdmin(Question question, User trader) {
        User investor = question.getUser();

        return QuestionsResponse.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .questionContent(question.getContent())
                .strategyName(question.getStrategy().getStrategyName())
                .stateCondition(question.getQnaState())
                .createdAt(question.getCreatedAt())
                .investor(BaseQnAInfoDto.builder()
                        .id(investor.getUserId())
                        .userName(investor.getUserName())
                        .profileImageUrl(investor.getImageUrl())
                        .build())
                .trader(BaseQnAInfoDto.builder()
                        .id(trader.getUserId())
                        .userName(trader.getUserName())
                        .profileImageUrl(trader.getImageUrl())
                        .build())
                .build();
    }
}
