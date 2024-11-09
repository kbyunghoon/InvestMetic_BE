package com.investmetic.domain.review.dto.response;

import com.investmetic.domain.review.model.entity.Review;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {
    private Long reviewId; // 단일 PK
    private String content;
    private int starRating;
    private LocalDateTime createdAt;

    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .createdAt(review.getCreatedAt())
                .build();

    }
}
