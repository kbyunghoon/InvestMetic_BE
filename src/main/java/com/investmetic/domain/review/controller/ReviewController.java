package com.investmetic.domain.review.controller;

import com.investmetic.domain.review.dto.request.ReviewRequestDto;
import com.investmetic.domain.review.dto.response.ReviewResponseDto;
import com.investmetic.domain.review.service.ReviewService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO : 스프링시큐리티 적용완료되면 양식에 맞게 @AuthenticationPrincipal로 userId수정
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 등록
    @PostMapping("/api/strategies/{strategyId}/reviews")
    public BaseResponse<ReviewResponseDto> addReview(
            @PathVariable Long strategyId,
            @RequestParam Long userId, // 임시로 userId를 쿼리 파라미터로 받음
            @RequestBody ReviewRequestDto reviewRequestDto) {

        ReviewResponseDto response = reviewService.addReview(strategyId, userId, reviewRequestDto);
        return BaseResponse.success(SuccessCode.CREATED, response);
    }

    // 리뷰 수정
    @PatchMapping("/api/strategies/{strategyId}/reviews/{reviewId}")
    public BaseResponse<ReviewResponseDto> updateReview(
            @PathVariable Long strategyId,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto reviewRequestDto) {

        ReviewResponseDto response = reviewService.updateReview(strategyId, reviewId, reviewRequestDto);
        return BaseResponse.success(SuccessCode.UPDATED, response);
    }

    // 리뷰 삭제
    @DeleteMapping("/api/strategies/{strategyId}/reviews/{reviewId}")
    public BaseResponse<ReviewResponseDto> deleteReview(
            @PathVariable Long strategyId,
            @PathVariable Long reviewId) {

        reviewService.deleteReview(strategyId, reviewId);
        return BaseResponse.success();
    }
}
