package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;


    public Question toEntity(User user, Strategy strategy) {
        return Question.createQuestion(user, strategy, title, content);
    }

}
