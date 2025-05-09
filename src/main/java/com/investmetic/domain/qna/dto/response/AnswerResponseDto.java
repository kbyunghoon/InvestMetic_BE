package com.investmetic.domain.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.investmetic.domain.user.model.Role;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerResponseDto {
    private Long answerId;
    private String content;
    private String nickname;
    private Role role;
    private String profileImageUrl;
    private LocalDateTime createdAt;
}
