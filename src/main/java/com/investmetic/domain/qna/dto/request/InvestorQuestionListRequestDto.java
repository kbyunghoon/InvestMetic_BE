package com.investmetic.domain.qna.dto.request;

import com.investmetic.domain.qna.model.QnaState;
import lombok.Getter;

@Getter
public class InvestorQuestionListRequestDto {
    private String keyword= "";
    private QnaState qnaState= QnaState.WAITING;
    private String sort = "DESC"; // 정렬 방식 ("DESC" 또는 "ASC")
    private String sortBy = "createdAt"; // 정렬 기준 ("createdAt", "qnaState")
}
