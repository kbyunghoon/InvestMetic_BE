package com.investmetic.domain.user.repository;


import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.subscription.model.entity.Subscription;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.domain.user.dto.response.TraderProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TraderListRepositoryTest {

    private final List<Role> roles = new ArrayList<>(
            List.of(Role.TRADER, Role.TRADER_ADMIN, Role.INVESTOR, Role.INVESTOR_ADMIN, Role.SUPER_ADMIN));
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StrategyRepository strategyRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TradeTypeRepository tradeTypeRepository;

    // 10명이 trader
    @BeforeEach
    void createUsers50() {
        Strategy strategy = null;
        TradeType tradeType = new TradeType(1L, "파괴적",true,"asdf.jpg");

        tradeTypeRepository.save(tradeType);

        for (int i = 0; i < 50; i++) {

            DecimalFormat dc = new DecimalFormat("##");

            User user = User.builder().userName("정룡우" + i).nickname("jeongRyongWoo" + i)
                    .email("jlwoo0925" + i + "@gmail.com").password("asdf" + i)
                    .imageUrl("jrw_projectS3/profile/정룡우.img").phone("010123456" + dc.format(i)).birthDate("000925")
                    .ipAddress("127.0.0.1").infoAgreement(Boolean.FALSE).userState(UserState.ACTIVE)
                    .role(roles.get(i % 5)).build();

            userRepository.save(user);

            if (user.getRole() == Role.TRADER||user.getRole() == Role.TRADER_ADMIN) {
                strategy = Strategy.builder().user(user).tradeType(tradeType).strategyName("전략" + i).isPublic(IsPublic.PUBLIC)
                        .isApproved(IsApproved.APPROVED).build();

                strategyRepository.save(strategy);
            }

            if(strategy != null) {
                 Subscription subscription = Subscription.builder().user(user).strategy(strategy).build();
                 subscriptionRepository.save(subscription);
            }

        }
    }


    @Test
    @DisplayName("트레이더 목록 조회 테스트.")
    void TraderListRepositoryTest1() {
        //구독순 확인.
        Pageable pageable = PageRequest.of(0, 4);
        Page<TraderProfileDto> page =userRepository.getTraderListPage(null,null,pageable);
        for (TraderProfileDto traderProfileDto : page.getContent()) {
            System.out.println(traderProfileDto);
        }
    }

}
