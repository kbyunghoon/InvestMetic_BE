package com.investmetic.domain.qna.dto.response;

import com.investmetic.global.common.PageResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionsPageResponse {
    private final PageResponseDto<QuestionsResponse> page;

    @Builder
    public QuestionsPageResponse(PageResponseDto<QuestionsResponse> page) {
        this.page = page;
    }

    public static QuestionsPageResponse from(PageResponseDto<QuestionsResponse> pageResponseDto) {
        return QuestionsPageResponse.builder()
                .page(pageResponseDto)
                .build();
    }
}
