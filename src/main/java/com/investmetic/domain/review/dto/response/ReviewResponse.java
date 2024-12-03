package com.investmetic.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.investmetic.domain.review.model.entity.Review;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
public class ReviewResponse {
    private final Long reviewId; // 단일 PK
    private final String content;
    private final int starRating;
    private final LocalDateTime createdAt;

    @Builder
    private ReviewResponse(Long reviewId, String content, int starRating, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.content = content;
        this.starRating = starRating;
        this.createdAt = createdAt;
    }

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
