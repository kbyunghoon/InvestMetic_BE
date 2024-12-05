package com.investmetic.domain.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class QuestionsDetailResponse {
    private Long questionId; // 문의 ID
    private String title; // 문의 제목
    private String questionContent; // 문의 내용
    private String answerContent; // 답변 내용
    private String strategyName; // 전략 이름
    private String profileImageUrl; // 프로필 이미지 URL
    private String nickname; // 투자자, 트레이더 이름
    private String state; // 문의 상태
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime questionCreatedAt; // 문의 생성일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime answerCreatedAt; // 답변 생성일
    private AnswerResponseDto answer;
}
