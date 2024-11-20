package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnswerRequestDto {
    @NotBlank
    private String content;

    public Answer toEntity(Question question) {
        return Answer.createAnswer(question, content);
    }
}
