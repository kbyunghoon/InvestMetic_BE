package com.investmetic.domain.user.service.logic;

import com.investmetic.domain.review.model.entity.Review;
import com.investmetic.domain.review.repository.ReviewRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.service.StrategyService;
import com.investmetic.domain.subscription.model.entity.Subscription;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommonLogic {

    private final StrategyRepository strategyRepository;
    private final StrategyService strategyService;
    private final SubscriptionRepository subscriptionRepository;
    private final ReviewRepository reviewRepository;

    public void deleteUser(User user) {

        // 해당 유저의 구독목록 가지고 오기.
        List<Subscription> userSubscriptionList = subscriptionRepository.findAllByUserUserId(user.getUserId());

        // 해당 회원이 전략을 구독 했는지 확인.
        if (!userSubscriptionList.isEmpty()) {
            // 해당 회원이 구독한 모든 전략의 구독 수 줄이기.
            for (Subscription subscription : userSubscriptionList) {
                Strategy strategy = strategyRepository.findById(subscription.getStrategy().getStrategyId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

                strategy.minusSubscriptionCount();
            }
            // 모든 구독 지우기.
            subscriptionRepository.deleteAllInBatch(userSubscriptionList);
        }

        List<Review> userReviewList = reviewRepository.findAllByUserUserId(user.getUserId());

        // 자신이 남긴 모든 리뷰 목록 지우기.
        if (!userReviewList.isEmpty()) {
            reviewRepository.deleteAllInBatch(userReviewList);
        }

        // 문의 삭제 추가 필요.

        // 해당 유저가 투자자면 메서드 종료
        if (Role.isInvestor(user.getRole())) {
            return;
        }

        // 자신의 전략 모두 조회.
        List<Strategy> strategyList = strategyRepository.findAllByUserUserId(user.getUserId());

        // 자신의 전략이 있는지 확인.
        if (!strategyList.isEmpty()) {
            // 자신의 전략 하나씩 순회.
            for (Strategy strategy : strategyList) {
                strategyService.deleteStrategy(strategy.getStrategyId());
            }
        }
    }
}
