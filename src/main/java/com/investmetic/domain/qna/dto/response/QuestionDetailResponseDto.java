package com.investmetic.domain.qna.dto.response;

import com.investmetic.domain.qna.model.QnaState;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionDetailResponseDto {
    private final Long questionId;
    private final String title;
    private final String content;
    private final String investorName;
    private final String traderName;
    private final QnaState qnaState;
    private final String strategyName;
    private LocalDateTime createdAt;
    private final Long answerId;
}
