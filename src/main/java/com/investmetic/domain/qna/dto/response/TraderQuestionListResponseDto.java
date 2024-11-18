package com.investmetic.domain.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Question;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraderQuestionListResponseDto {
    private Long questionId;
    private String title;
    private QnaState qnaState;
    private String strategyName;
    private String investorName;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static com.investmetic.domain.qna.dto.response.TraderQuestionListResponseDto from(Question question) {
        return com.investmetic.domain.qna.dto.response.TraderQuestionListResponseDto.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .qnaState(question.getQnaState())
                .strategyName(question.getStrategy().getStrategyName())
                .investorName(question.getUser().getNickname())
                .createdAt(question.getCreatedAt())
                .build();
    }
}

