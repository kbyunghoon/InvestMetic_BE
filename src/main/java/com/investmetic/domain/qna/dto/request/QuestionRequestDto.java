package com.investmetic.domain.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionRequestDto {
    @NotNull(message = "문의 제목을 입력해주세요.")
    @NotBlank(message = "문의 제목을 입력해주세요.")
    private final String title;

    @NotNull(message = "문의 내용을 입력해주세요.")
    @NotBlank(message = "문의 내용을 입력해주세요.")
    private final String content;

}
