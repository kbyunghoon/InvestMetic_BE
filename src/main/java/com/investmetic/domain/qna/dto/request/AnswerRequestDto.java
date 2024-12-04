package com.investmetic.domain.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnswerRequestDto {
    @NotNull(message = "답변 내용을 입력해주세요.")
    @NotBlank(message = "답변 내용을 입력해주세요.")
    private String content;
}
