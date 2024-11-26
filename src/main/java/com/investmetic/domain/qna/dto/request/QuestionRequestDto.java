package com.investmetic.domain.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionRequestDto {

    @NotBlank(message = "문의 제목을 입력해주세요.")
    private final String title;

    @NotBlank(message = "문의 내용을 입력해주세요.")
    private final String content;

    @Builder
    public QuestionRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
