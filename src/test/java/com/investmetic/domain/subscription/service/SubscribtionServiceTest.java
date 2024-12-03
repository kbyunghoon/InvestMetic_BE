package com.investmetic.domain.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.subscription.model.entity.Subscription;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class SubscribtionServiceTest {
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StrategyRepository strategyRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private TradeTypeRepository tradeTypeRepository;

    private Strategy testStrategy;
    private User testUser;
    private User anotherUser;
    private TradeType testTradeType;

    @BeforeEach
    public void setup() {
        testUser = userRepository.save(TestEntityFactory.createTestUser("testUser", "testuser@example.com"));
        anotherUser = userRepository.save(TestEntityFactory.createTestUser("anotherUser", "another@example.com"));
        testTradeType = tradeTypeRepository.save(TestEntityFactory.createTestTradeType());
        testStrategy = strategyRepository.save(TestEntityFactory.createTestStrategy(testUser, testTradeType));
    }

    @DisplayName("구독하기 테스트")
    @Test
    public void subscribe() {

        int initialSubscriptionCount = testStrategy.getSubscriptionCount();
        Long strategyId = testStrategy.getStrategyId();
        Long userId = anotherUser.getUserId();

        subscriptionService.subscribe(strategyId, userId);

        // 구독 확인
        Optional<Subscription> subscription = subscriptionRepository.findByStrategyIdAndUserId(strategyId, userId);

        assertThat(subscription.isPresent()).isTrue(); // 구독이 생성되었는지 확인
        assertThat(subscription.get().getStrategy().getStrategyId()).isEqualTo(strategyId); // 전략 ID가 올바른지 확인
        assertThat(subscription.get().getUser().getUserId()).isEqualTo(userId); // 사용자 ID가 올바른지 확인

        //구독 수 증가 확인
        Integer subscriptionCount = strategyRepository.findById(strategyId).get().getSubscriptionCount();
        assertThat(subscriptionCount).isEqualTo(initialSubscriptionCount+1); // 구독 수가 1 증가했는지 확인

        // 구독 취소 확인
        subscriptionService.subscribe(strategyId, userId);

        // 구독 수 감소 확인
        subscriptionCount = strategyRepository.findById(strategyId).get().getSubscriptionCount();
        assertThat(subscriptionCount).isEqualTo(initialSubscriptionCount); // 구독 수가 감소했는지 확인

    }

    @DisplayName("본인전략의 구독할때 예외테스트")
    @Test
    public void 구독_예외_테스트() {

        // Given
        Strategy strategy = strategyRepository.findAll().get(0); // 첫 번째 전략
        Long strategyId = strategy.getStrategyId();
        Long userId = strategy.getUser().getUserId(); // 전략 소유자 ID

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> subscriptionService.subscribe(strategyId, userId) // 본인의 전략을 구독 시도
        );

        // 예외 검증
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SELF_SUBSCRIPTION_NOT_ALLOWED);
        assertThat(exception.getMessage()).isEqualTo("본인 전략에는 구독할 수 없습니다.");
    }

}
