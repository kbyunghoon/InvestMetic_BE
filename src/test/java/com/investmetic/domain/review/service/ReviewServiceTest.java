package com.investmetic.domain.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.review.dto.request.ReviewRequestDto;
import com.investmetic.domain.review.dto.response.ReviewResponse;
import com.investmetic.domain.review.repository.ReviewRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TradeTypeRepository tradeTypeRepository;

    @Autowired
    private EntityManager em; // EntityManager를 통해 flush() 사용

    private Strategy testStrategy;
    private User testUser;
    private TradeType testTradeType;

    @BeforeEach
    public void setup() {
        testUser = userRepository.save(TestEntityFactory.createTestUser());
        testTradeType = tradeTypeRepository.save(TestEntityFactory.createTestTradeType());
        testStrategy = strategyRepository.save(TestEntityFactory.createTestStrategy(testUser, testTradeType));
    }

    private void addReviewWithInvalidStrategyId() {
        reviewService.addReview(-1L, testUser.getUserId(),
                ReviewRequestDto.builder().content("Great strategy!").starRating(5).build());
    }

    @DisplayName("전략을 찾을 수 없을 때 예외 발생")
    @Test
    void givenInvalidStrategyId_whenAddReview_thenThrowsException() {
        assertThrows(BusinessException.class, this::addReviewWithInvalidStrategyId);
    }

    private void updateReviewWithInvalidId(ReviewRequestDto updateDto) {
        reviewService.updateReview(testStrategy.getStrategyId(), -1L, updateDto);
    }

    @DisplayName("리뷰를 찾을 수 없을 때 예외 발생")
    @Test
    void givenInvalidReviewId_whenUpdateReview_thenThrowsException() {
        ReviewRequestDto updateDto = ReviewRequestDto.builder()
                .content("Updated content")
                .starRating(4)
                .build();

        assertThrows(BusinessException.class, () -> updateReviewWithInvalidId(updateDto));
    }

    @DisplayName("본인의 전략에 리뷰를 등록할 수 없을 때 예외 발생")
    @Test
    void 리뷰등록_예외테스트1() {
        ReviewRequestDto requestDto = ReviewRequestDto.builder()
                .content("전략 굿")
                .starRating(5)
                .build();

        // 본인의 전략에 리뷰 등록 시도
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> reviewService.addReview(testStrategy.getStrategyId(), testUser.getUserId(), requestDto)
        );

        // 예외 메시지 및 코드 검증
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CANNOT_REVIEW_OWN_STRATEGY);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.CANNOT_REVIEW_OWN_STRATEGY.getMessage());
    }

    @DisplayName("같은 전략에 중복 리뷰 등록 시 예외 발생")
    @Test
    void 리뷰등록_예외테스트2() {
        ReviewRequestDto requestDto = ReviewRequestDto.builder()
                .content("전략 굿")
                .starRating(5)
                .build();

        User newUser = User.builder()
                .userName("newUser")
                .nickname("newUser")
                .phone("01012345678")
                .birthDate("19900101")
                .password("password")
                .email("test@example.com")
                .role(Role.INVESTOR)
                .infoAgreement(true)
                .build();

        userRepository.save(newUser);

        // 첫 번째 리뷰 등록
        reviewService.addReview(testStrategy.getStrategyId(), newUser.getUserId(), requestDto);

        em.flush();
        em.clear();

        // 같은 전략에 중복 리뷰 등록 시도
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> reviewService.addReview(testStrategy.getStrategyId(), newUser.getUserId(), requestDto)
        );

        // 예외 메시지 및 코드 검증
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_REVIEW);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.DUPLICATE_REVIEW.getMessage());
    }


    @DisplayName("리뷰 등록 시 리뷰 개수와 평균 별점 업데이트")
    @Test
    void 리뷰등록_테스트() {
        ReviewRequestDto requestDto = ReviewRequestDto.builder()
                .content("전략 굿")
                .starRating(5)
                .build();

        int initialCount = reviewRepository.countByStrategy(testStrategy);

        // 리뷰 추가
        reviewService.addReview(testStrategy.getStrategyId(), testUser.getUserId(), requestDto);

        em.flush();
        em.clear();

        // 리뷰 개수 검증
        int updatedCount = reviewRepository.countByStrategy(testStrategy);
        assertThat(updatedCount).isEqualTo(initialCount + 1);

        Strategy updatedStrategy = strategyRepository.findById(testStrategy.getStrategyId()).orElseThrow();

        //전략의 리뷰갯수 검증
        assertThat(updatedCount).isEqualTo(updatedStrategy.getReviewCount());

        // 평균 별점 검증
        assertThat(updatedStrategy.getAverageRating()).isEqualTo(5.0);
    }

    @DisplayName("리뷰 5개 등록 시 리뷰 개수 및 평균 별점 테스트 ")
    @Test
    void 리뷰등록_테스트2() {

        for (int i = 1; i <= 5; i++) {
            ReviewRequestDto requestDto = ReviewRequestDto.builder()
                    .content("전략 굿")
                    .starRating(i)
                    .build();
            reviewService.addReview(testStrategy.getStrategyId(), testUser.getUserId(), requestDto);
        }

        em.flush();
        em.clear();

        // 리뷰 개수 검증
        int updatedCount = reviewRepository.countByStrategy(testStrategy);
        assertThat(updatedCount).isEqualTo(5);

        Strategy updatedStrategy = strategyRepository.findById(testStrategy.getStrategyId()).orElseThrow();

        // 전략의 리뷰갯수 검증
        assertThat(updatedCount).isEqualTo(updatedStrategy.getReviewCount());

        // 평균 별점 검증
        assertThat(updatedStrategy.getAverageRating()).isEqualTo(3.0);
    }

    @DisplayName("리뷰 수정 시 리뷰 개수와 평균 별점 업데이트")
    @Test
    void 리뷰수정_테스트() {

        // 기존 리뷰 등록
        ReviewRequestDto initialRequest = ReviewRequestDto.builder()
                .content("전략 굿")
                .starRating(4)
                .build();
        ReviewResponse response = reviewService.addReview(testStrategy.getStrategyId(), testUser.getUserId(),
                initialRequest);

        em.flush();
        em.clear();

        int initialCount = reviewRepository.countByStrategy(testStrategy);

        // 리뷰 수정
        ReviewRequestDto updateRequest = ReviewRequestDto.builder()
                .content("전략 수정")
                .starRating(3)
                .build();
        reviewService.updateReview(testStrategy.getStrategyId(), response.getReviewId(), updateRequest);

        em.flush();
        em.clear();

        // 리뷰 개수 검증 (수정 후에도 동일)
        int updatedCount = reviewRepository.countByStrategy(testStrategy);
        assertThat(updatedCount).isEqualTo(initialCount);

        Strategy updatedStrategy = strategyRepository.findById(testStrategy.getStrategyId()).orElseThrow();

        // 전략의 리뷰갯수 검증
        assertThat(updatedCount).isEqualTo(updatedStrategy.getReviewCount());

        // 평균 별점 검증 (평점이 4 -> 3으로 업데이트됨)
        assertThat(updatedStrategy.getAverageRating()).isEqualTo(3.0);
    }

    @DisplayName("1개의 리뷰만 있을때, 리뷰삭제 테스트")
    @Test
    void 리뷰삭제_테스트() {
        // 기존 리뷰 등록
        ReviewRequestDto requestDto = ReviewRequestDto.builder()
                .content("전략 굿")
                .starRating(4)
                .build();
        ReviewResponse response = reviewService.addReview(testStrategy.getStrategyId(), testUser.getUserId(),
                requestDto);

        em.flush();
        em.clear();

        int initialCount = reviewRepository.countByStrategy(testStrategy);

        // 리뷰 삭제
        reviewService.deleteReview(testStrategy.getStrategyId(), response.getReviewId());

        em.flush();
        em.clear();

        // 리뷰 개수 검증 (삭제 후 감소)
        int updatedCount = reviewRepository.countByStrategy(testStrategy);
        assertThat(updatedCount).isEqualTo(initialCount - 1);

        Strategy updatedStrategy = strategyRepository.findById(testStrategy.getStrategyId()).orElseThrow();

        // 전략의 리뷰갯수 검증
        assertThat(updatedCount).isEqualTo(updatedStrategy.getReviewCount());

        // 평균 별점 검증 (리뷰 삭제 후 리뷰가 없으면 0.0으로 설정)
        assertThat(updatedStrategy.getAverageRating()).isEqualTo(0.0);
    }


    @DisplayName("5개의 리뷰만 있을때, 특정 리뷰삭제 테스트")
    @Test
    void 리뷰삭제_테스트2() {
        //  리뷰 5개등록
        List<Long> reviewIds = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ReviewRequestDto requestDto = ReviewRequestDto.builder()
                    .content("전략 굿")
                    .starRating(i)
                    .build();
            ReviewResponse review = reviewService.addReview(testStrategy.getStrategyId(),
                    testUser.getUserId(), requestDto);
            reviewIds.add(review.getReviewId());
        }

        em.flush();
        em.clear();

        int initialCount = reviewRepository.countByStrategy(testStrategy);

        // 5점짜리 리뷰의 ID를 찾아 삭제
        Long reviewIdToDelete = reviewIds.get(4); // 5번째 리뷰 (평점 5)
        reviewService.deleteReview(testStrategy.getStrategyId(), reviewIdToDelete);

        em.flush();
        em.clear();

        // 리뷰 개수 검증 (삭제 후 감소)
        int updatedCount = reviewRepository.countByStrategy(testStrategy);
        assertThat(updatedCount).isEqualTo(initialCount - 1);

        Strategy updatedStrategy = strategyRepository.findById(testStrategy.getStrategyId()).orElseThrow();

        // 전략의 리뷰갯수 검증
        assertThat(updatedCount).isEqualTo(updatedStrategy.getReviewCount());

        // 평균 별점 검증 (5점 삭제 후 남은 리뷰 평균: (1+2+3+4) / 4 = 2.5)
        assertThat(updatedStrategy.getAverageRating()).isEqualTo(2.5);
    }
}