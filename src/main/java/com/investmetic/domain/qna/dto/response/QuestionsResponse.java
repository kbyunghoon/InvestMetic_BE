package com.investmetic.domain.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class QuestionsResponse {
    private final Long questionId; // 문의 ID
    private final String title; // 문의 제목
    private final String questionContent; // 문의 내용
    private final String strategyName; // 전략 이름
    private final String profileImageUrl; // 프로필 이미지 URL
    private final String nickname; // 투자자, 트레이더 이름
    private final String stateCondition; // 문의 상태
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt; // 문의 생성일
    private AnswerResponseDto answerResponseDto;
}
