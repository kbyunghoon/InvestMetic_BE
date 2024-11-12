package com.investmetic.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.investmetic.domain.review.model.entity.Review;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewResponse {
    private final Long reviewId; // 단일 PK
    private final String content;
    private final int starRating;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getContent(),
                review.getStarRating(),
                review.getCreatedAt()
        );
    }
}
