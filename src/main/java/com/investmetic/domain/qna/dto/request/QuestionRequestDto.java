package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionRequestDto {
    @NotNull
    private String title;
    @NotNull
    private String content;
    @Builder
    public QuestionRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Question toEntity(User user, Strategy strategy) {
        return Question.createQuestion(user, strategy, title, content);
    }

}
