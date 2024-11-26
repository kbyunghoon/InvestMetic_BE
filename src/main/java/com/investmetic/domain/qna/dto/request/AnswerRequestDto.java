package com.investmetic.domain.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnswerRequestDto {
    @NotBlank(message = "문의 내용을 입력해주세요.")
    private String content;

    @Builder
    public AnswerRequestDto(String content){
        this.content = content;
    }
}
