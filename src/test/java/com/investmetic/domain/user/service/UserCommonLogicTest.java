package com.investmetic.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.review.repository.ReviewRepository;
import com.investmetic.domain.strategy.service.StrategyListingService;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.domain.user.service.logic.UserCommonLogic;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("회원 삭제 로직")
class UserCommonLogicTest {

    @Autowired
    private UserCommonLogic userCommonLogic;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StrategyListingService strategyListingService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager em;

    /*
     * MockDB위주로 설정.
     * */
    @Test
    @DisplayName("회원 Trader 삭제 확인")
    void deleteUserTest1() {
        User trader = userRepository.findById(1L).orElse(null);

        Pageable pageable = PageRequest.of(0, 10);

        if (trader != null && Role.isTrader(trader.getRole())) {

            // when
            userCommonLogic.deleteUser(trader);
            em.flush();
            em.clear();

            // then
            // 해당 트레이더의 전략이 없어야함.
            assertThat(strategyListingService.getMyStrategies(trader.getUserId(), pageable).getContent())
                    .isEmpty();

            //  strategyService.deleteStrategy()에서 test 검증 했으므로 일간, 월간 분석 Data등은 통과.

            // 해당 트레이더가 구독한 전략이 없어야함.
            assertThat(strategyListingService.getSubscribedStrategies(trader.getUserId(), pageable).getContent())
                    .isEmpty();

            // 자신이 남긴 리뷰가 없어야함.
            assertThat(reviewRepository.findAllByUserUserId(trader.getUserId()))
                    .isEmpty();
        }
    }

    @Test
    @DisplayName("회원 Investor 삭제 확인")
    void deleteUserTest2() {

        // 투자자
        User investor = userRepository.findById(2L).orElse(null);

        Pageable pageable = PageRequest.of(0, 10);

        if (investor != null && Role.isTrader(investor.getRole())) {

            // 해당 회원이 구독한 전략이 있음을 확인.
            assertThat(strategyListingService.getSubscribedStrategies(investor.getUserId(), pageable).getContent())
                    .isNotEmpty();

            // when
            userCommonLogic.deleteUser(investor);
            em.flush();
            em.clear();

            // then

            // 해당 투자자가 구독한 전략이 없어야함.
            assertThat(strategyListingService.getSubscribedStrategies(investor.getUserId(), pageable).getContent())
                    .isEmpty();

            // 자신이 남긴 리뷰가 없어야함.
            assertThat(reviewRepository.findAllByUserUserId(investor.getUserId()))
                    .isEmpty();

        }


    }


}
