package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionRequestDto {
    private final String keyword;
    private final SearchCondition searchCondition;
    private final StateCondition stateCondition;

    @NotBlank(message = "문의 제목을 입력해주세요.")
    private final String title;

    @NotBlank(message = "문의 내용을 입력해주세요.")
    private final String content;

}
