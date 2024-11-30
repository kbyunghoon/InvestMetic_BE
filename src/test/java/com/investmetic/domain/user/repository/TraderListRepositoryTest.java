package com.investmetic.domain.user.repository;


import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.domain.user.dto.object.TraderListSort;
import com.investmetic.domain.user.dto.response.TraderProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@Transactional
class TraderListRepositoryTest {

    private static final List<Role> roles = new ArrayList<>(
            List.of(Role.TRADER, Role.TRADER_ADMIN, Role.INVESTOR, Role.INVESTOR_ADMIN, Role.SUPER_ADMIN));

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StrategyRepository strategyRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TradeTypeRepository tradeTypeRepository;

    // 트레이더 생성.... 더 자세하게 보려면 데이터가 필요하겠네요.
    @BeforeAll
    void createUsers50() {
        Strategy strategy1 = null;
        Strategy strategy2 = null;
        TradeType tradeType = TestEntityFactory.createTestTradeType();

        tradeTypeRepository.save(tradeType);

        for (int i = 0; i < 50; i++) {

            DecimalFormat dc = new DecimalFormat("##");

            User user = User.builder()
                    .userName("정룡우" + i)
                    .nickname("jeongRyongWoo" + i)
                    .email("jlwoo0925" + i + "@gmail.com")
                    .password("asdf" + i)
                    .imageUrl("jrw_projectS3/profile/정룡우.img")
                    .phone("010123456" + dc.format(i))
                    .birthDate("000925")
                    .ipAddress("127.0.0.1")
                    .infoAgreement(Boolean.FALSE)
                    .userState(UserState.ACTIVE)
                    .role(roles.get(i % 5)).build();

            userRepository.save(user);

            // Trader 전략 만들기.
            if ((user.getRole() == Role.TRADER || user.getRole() == Role.TRADER_ADMIN) && (i < 40)) {
                strategy1 = Strategy.builder()
                        .user(user)
                        .tradeType(tradeType)
                        .strategyName("전략" + i)
                        .subscriptionCount((int) (Math.random() * i * 10))
                        .isPublic(IsPublic.PUBLIC)
                        .isApproved(IsApproved.APPROVED)
                        .build();

                // Role이 Trader인 사람은 전략을 2개씩 가짐.
                if (user.getRole() == Role.TRADER) {
                    strategy2 = Strategy.builder()
                            .user(user)
                            .tradeType(tradeType)
                            .strategyName("전략" + i)
                            .subscriptionCount((int) (Math.random() * i * 10))
                            .isPublic(IsPublic.PUBLIC)
                            .isApproved(IsApproved.APPROVED)
                            .build();
                    strategyRepository.save(strategy2);
                }
                strategyRepository.save(strategy1);
            }
        }
    }

    @AfterAll
    void deleteAll() {
        //constraint -> 연관 관계 순서대로
        subscriptionRepository.deleteAll();
        strategyRepository.deleteAll();
        tradeTypeRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("트레이더 목록 조회 테스트.(구독수 순)")
    void TraderListRepositoryTest1() {

        // given 가장 큰수로 설정
        Integer bigger = Integer.MAX_VALUE;
        int pagesize = 100;
        TraderListSort sort = TraderListSort.SUBSCRIBE_TOTAL;

        Pageable pageable = PageRequest.of(0, pagesize);

        // when
        Page<TraderProfileDto> page = userRepository.getTraderListPage(sort, null, pageable);

        // then
        //구독순 확인.
        for (int i = 1; i <= page.getTotalPages(); i++) {

            for (TraderProfileDto traderProfileDto : page.getContent()) {
                // 앞순서의 트레이너의 구독자 수보다 작거나 같아야함.
                assertThat(traderProfileDto.getTotalSubCount()).isLessThanOrEqualTo(bigger);

                // 현재 순서의 트레이너 구독자 수 대입.
                bigger = traderProfileDto.getTotalSubCount();
            }

            // 해당 페이지가 마지막 페이지이면 종료.
            if (i == page.getTotalPages()) {
                break;
            }

            // 페이지 증가시키면서 확인.
            Pageable nextPage = PageRequest.of(i, pagesize);
            page = userRepository.getTraderListPage(sort, null, nextPage);
        }
    }


    @Test
    @DisplayName("트레이더 목록 조회 테스트. (전략수순)")
    void TraderListRepositoryTest2() {

        // given 가장 큰수로 설정
        long bigger = Integer.MAX_VALUE;
        int pagesize = 5;
        TraderListSort sort = TraderListSort.STRATEGY_TOTAL;

        Pageable pageable = PageRequest.of(0, pagesize);

        // when - (orderBy = STRATEGY_TOTAL)
        Page<TraderProfileDto> page = userRepository.getTraderListPage(sort, null, pageable);

        // then
        // 전략수 순
        for (int i = 1; i <= page.getTotalPages(); i++) {

            for (TraderProfileDto traderProfileDto : page.getContent()) {
                // 앞순서의 트레이너가 가진 전략수보다 작거나 같아야함.
                assertThat(traderProfileDto.getStrategyCount()).isLessThanOrEqualTo(bigger);

                // 현재 순서의 트레이너 전략수 대입.
                bigger = traderProfileDto.getStrategyCount();
            }
            System.out.println(page.getContent().size());

            if (i == page.getTotalPages()) {
                break;
            }

            Pageable nextPage = PageRequest.of(i, pagesize);
            page = userRepository.getTraderListPage(sort, null, nextPage);
        }
    }


    @Test
    @DisplayName("트레이더 목록 조회 테스트.(닉네임 조회)")
    void TraderListRepositoryTest3() {

        // given
        int bigger = Integer.MAX_VALUE;
        int pagesize = 5;
        String keyword = "2";
        TraderListSort sort = TraderListSort.SUBSCRIBE_TOTAL;

        Pageable pageable = PageRequest.of(0, pagesize);

        // when - 닉네임에 2가 들어가는지
        Page<TraderProfileDto> page = userRepository.getTraderListPage(sort, keyword, pageable);

        // then
        //구독순 확인, 닉네임에 해당 keyword가 들어가는지 확인.
        for (int i = 1; i <= page.getTotalPages(); i++) {

            for (TraderProfileDto traderProfileDto : page.getContent()) {

                // 앞순서의 트레이너의 구독자 수보다 작거나 같아야함.
                assertThat(traderProfileDto.getTotalSubCount()).isLessThanOrEqualTo(bigger);

                //닉네임에 해당 keyword가 들어가는지 확인.
                assertThat(traderProfileDto.getNickname()).contains(keyword);

                // 현재 순서의 트레이너 구독자 수 대입.
                bigger = traderProfileDto.getTotalSubCount();
            }

            // 해당 페이지가 마지막 페이지이면 종료.
            if (i == page.getTotalPages()) {
                break;
            }

            // 페이지 증가시키면서 확인.
            Pageable nextPage = PageRequest.of(i, pagesize);
            page = userRepository.getTraderListPage(sort, null, nextPage);
        }
    }


}

