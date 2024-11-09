package com.investmetic.domain.review.service;

import com.investmetic.domain.review.dto.request.ReviewRequestDto;
import com.investmetic.domain.review.dto.response.ReviewResponseDto;
import com.investmetic.domain.review.model.entity.Review;
import com.investmetic.domain.review.repository.ReviewRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BaseException;
import com.investmetic.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StrategyRepository strategyRepository;
    private final UserRepository userRepository;

    //리뷰 등록
    @Transactional
    public ReviewResponseDto addReview(Long strategyId, Long userId, ReviewRequestDto reviewRequestDto) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BaseException(ErrorCode.STRATEGY_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.ENTITY_NOT_FOUND));

        Review review = reviewRequestDto.toEntity(user, strategy);

        // 평균 별점 업데이트
        addUpdateAverageRating(strategy, review.getStarRating());

        reviewRepository.save(review);
        return ReviewResponseDto.from(review);

    }

    //리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(Long strategyId, Long reviewId, ReviewRequestDto reviewRequestDto) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BaseException(ErrorCode.STRATEGY_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.REVIEW_NOT_FOUND));

        int oldStarRating = review.getStarRating();
        int newStarRating = reviewRequestDto.getStarRating();

        review.updateReview(reviewRequestDto.getContent(), reviewRequestDto.getStarRating());
        // 평균 별점 업데이트
        updateAverageRating(strategy, oldStarRating, newStarRating);

        return ReviewResponseDto.from(review);
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(Long strategyId, Long reviewId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BaseException(ErrorCode.STRATEGY_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.REVIEW_NOT_FOUND));

        int deletedStarRating = review.getStarRating();

        // 평균 별점 업데이트
        deleteUpdateAverageRating(strategy, deletedStarRating);
        reviewRepository.delete(review);
    }


    // 평균 별점 업데이트 - 리뷰 등록
    private void addUpdateAverageRating(Strategy strategy, int newStarRating) {
        int reviewCount = reviewRepository.countByStrategy(strategy);

        double currentAverage = strategy.getAverageRating();
        double updatedAverage = ((currentAverage * reviewCount) + newStarRating) / (reviewCount + 1);
        saveAverageRating(strategy, updatedAverage);
    }

    // 평균 별점 업데이트 - 리뷰 수정
    private void updateAverageRating(Strategy strategy, int oldStarRating, int newStarRating) {
        int reviewCount = reviewRepository.countByStrategy(strategy);
        double currentAverage = strategy.getAverageRating();

        // 이전 별점을 제거하고 새 별점을 추가한 평균 계산
        double updatedAverage = ((currentAverage * reviewCount) - oldStarRating + newStarRating) / reviewCount;
        saveAverageRating(strategy, updatedAverage);
    }

    // 평균 별점 업데이트 - 리뷰 삭제
    private void deleteUpdateAverageRating(Strategy strategy, int deletedStarRating) {
        int reviewCount = reviewRepository.countByStrategy(strategy);

        double currentAverage = strategy.getAverageRating();
        double updatedAverage;

        if (reviewCount - 1 == 0) {
            updatedAverage = 0.0;
        } else {
            updatedAverage = ((currentAverage * reviewCount) - deletedStarRating) / (reviewCount - 1);
        }
        saveAverageRating(strategy, updatedAverage);
    }

    private void saveAverageRating(Strategy strategy, double updatedAverage) {
        strategy.updateAverageRating(updatedAverage);
        strategyRepository.save(strategy);
    }
}
