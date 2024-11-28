package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class QuestionRequestDto {
    private final String keyword;
    private final SearchCondition searchCondition;
    private final StateCondition stateCondition;

    @NotBlank(message = "문의 제목을 입력해주세요.")
    private final String title;

    @NotBlank(message = "문의 내용을 입력해주세요.")
    private final String content;

    public QuestionRequestDto(String keyword, SearchCondition searchCondition, StateCondition stateCondition,
                              String title, String content) {
        this.keyword = keyword;
        this.searchCondition = searchCondition;
        this.stateCondition = stateCondition;
        this.title = title;
        this.content = content;
    }
}
