package com.investmetic.domain.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.subscription.model.entity.Subscription;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.List;
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

    @BeforeEach
    public void setUp() {
        User user = User.builder()
                .userName("testUser")
                .nickname("testNickname")
                .phone("01012345678")
                .birthDate("19900101")
                .password("password")
                .email("test@example.com")
                .role(Role.INVESTOR)
                .infoAgreement(true)
                .build();
        userRepository.save(user);
        Long strategyId = 1L;

        TradeType tradetype = TradeType.builder()
                .tradeTypeName("Sample_Trade1")
                .tradeTypeIconUrl("/icons/sample-icon1.png")
                .build();
        tradeTypeRepository.save(tradetype);
        Strategy strategy = Strategy.builder()
                .user(user)
                .strategyId(strategyId)
                .isPublic(IsPublic.PUBLIC)
                .tradeType(tradetype)
                .build();
        strategyRepository.save(strategy);

    }

    @Test
    public void subscribe() {
        List<Strategy> strategylist = strategyRepository.findAll();
        List<User> userlist = userRepository.findAll();
        Long strategyId = strategylist.get(0).getStrategyId();
        Long userId = userlist.get(0).getUserId();

        subscriptionService.subscribe(strategyId, userId);

        // 구독 확인
        Optional<Subscription> subscription = subscriptionRepository.findByStrategyIdAndUserId(strategyId, userId);
        assertThat(subscription.get().getId().equals(userId));
        assertThat(subscription.get().getUser().getUserId().equals(strategyId));
        //구독 수 확인
        Integer subscriptionCount = strategyRepository.findById(strategyId).get().getSubscriptionCount();
        assertThat(subscriptionCount).isEqualTo(1);
        // 구독 취소 확인
        subscriptionService.subscribe(strategyId, userId);

        assertThat(subscriptionRepository.findAll().size()).isEqualTo(0);
        subscriptionCount = strategyRepository.findById(strategyId).get().getSubscriptionCount();
        assertThat(subscriptionCount).isEqualTo(0);

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
