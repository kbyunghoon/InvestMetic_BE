package com.investmetic.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.investmetic.domain.review.model.entity.Review;
import com.investmetic.domain.user.model.entity.User;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewDetailResponse {
    private final Long reviewId;
    private final String nickname;
    private final String content;
    private final String imageUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    private final int starRating;

    // 정적 팩토리 메서드
    public static ReviewDetailResponse from(Review review, User user) {
        return new ReviewDetailResponse(
                review.getReviewId(),
                review.getNickname(),
                review.getContent(),
                user.getImageUrl(),
                review.getCreatedAt(),
                review.getStarRating()
        );
    }
}
