package com.investmetic.domain.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;

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
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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

}
