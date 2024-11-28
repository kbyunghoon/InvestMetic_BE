package com.investmetic.domain.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AnswerRequestDto {
    @NotBlank(message = "답변 내용을 입력해주세요.")
    private String content;

    @Builder
    public AnswerRequestDto(String content) {
        this.content = content;
    }
}
