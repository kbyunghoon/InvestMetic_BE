package com.investmetic.domain.review.controller;

import com.investmetic.domain.review.dto.request.ReviewRequestDto;
import com.investmetic.domain.review.dto.response.ReviewListResponse;
import com.investmetic.domain.review.dto.response.ReviewResponse;
import com.investmetic.domain.review.service.ReviewService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO : 스프링시큐리티 적용완료되면  @AuthenticationPrincipal로 userId수정
@RestController
@RequestMapping("/api/strategies/{strategyId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 등록
    @PostMapping
    public ResponseEntity<BaseResponse<ReviewResponse>> addReview(
            @PathVariable Long strategyId,
            @RequestParam Long userId, // 임시로 userId를 쿼리 파라미터로 받음
            @RequestBody @Valid ReviewRequestDto reviewRequestDto) {

        ReviewResponse result = reviewService.addReview(strategyId, userId, reviewRequestDto);
        return BaseResponse.success(SuccessCode.CREATED, result);
    }

    // 리뷰 수정
    @PatchMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<ReviewResponse>> updateReview(
            @PathVariable Long strategyId,
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewRequestDto reviewRequestDto) {

        ReviewResponse result = reviewService.updateReview(strategyId, reviewId, reviewRequestDto);
        return BaseResponse.success(SuccessCode.UPDATED, result);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<Void>> deleteReview(
            @PathVariable Long strategyId,
            @PathVariable Long reviewId) {

        reviewService.deleteReview(strategyId, reviewId);
        return BaseResponse.success();
    }

    // 리뷰 목록 조회
    @GetMapping
    public ResponseEntity<BaseResponse<ReviewListResponse>> getReviews(
            @PathVariable Long strategyId,
            @RequestParam Long userId, // 임시로 userId를 쿼리 파라미터로 받음
            @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

        ReviewListResponse result = reviewService.getReviewList(strategyId, userId, pageable);
        return BaseResponse.success(result);
    }
}
