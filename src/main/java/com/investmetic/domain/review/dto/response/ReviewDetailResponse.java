package com.investmetic.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.investmetic.domain.review.model.entity.Review;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewDetailResponse {
    private final Long reviewId;
    private final String content;
    private final String nickname;
    private final String imageUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    private final int starRating;

    @Builder
    private ReviewDetailResponse(Long reviewId, String content, String nickname, String imageUrl,
                                 LocalDateTime createdAt, int starRating) {
        this.reviewId = reviewId;
        this.content = content;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.starRating = starRating;
    }

    public static ReviewDetailResponse from(Review review) {
        return ReviewDetailResponse.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .nickname(review.getNickname())
                .imageUrl(review.getUser().getImageUrl())
                .createdAt(review.getCreatedAt())
                .starRating(review.getStarRating())
                .build();
    }
}