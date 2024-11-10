package com.investmetic.domain.review.dto.response;

import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.util.RoundUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewListResponse {
    private final double averageRating;
    private final PageResponseDto<ReviewDetailResponse> reviews;

    // 정적 팩토리 메서드
    public static ReviewListResponse createReviewListResponse(double averageRating,
                                                              PageResponseDto<ReviewDetailResponse> reviews) {
        return new ReviewListResponse(RoundUtil.roundToSecond(averageRating), reviews);
    }

}
