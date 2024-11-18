package com.investmetic.domain.qna.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QuestionDetailResponseDto {
    private final Long questionId;
    private final String title;
    private final String content;
    private final Long answerId;
}
