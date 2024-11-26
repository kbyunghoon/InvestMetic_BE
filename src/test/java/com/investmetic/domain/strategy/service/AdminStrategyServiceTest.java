package com.investmetic.domain.strategy.service;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
public class AdminStrategyServiceTest {
    @Autowired
    private AdminStrategyService adminStrategyService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StrategyRepository strategyRepository;
    @Autowired
    private TradeTypeRepository tradeTypeRepository;

    @Test
    @DisplayName("승인 상태 변경 테스트")
    void userDenyApproveTest() {

        // 더미 데이터 입력
        User user = TestEntityFactory.createTestUser();
        userRepository.save(user);
        TradeType tradeType = TestEntityFactory.createTestTradeType();
        tradeTypeRepository.save(tradeType);
        Strategy strategy = Strategy.builder()
                .user(user)
                .tradeType(tradeType)
                .strategyName("매매 전략")
                .operationCycle(OperationCycle.DAY)
                .minimumInvestmentAmount(MinimumInvestmentAmount.ABOVE_100M)
                .strategyDescription("전략상세")
                .proposalFilePath("http://~")
                .isPublic(IsPublic.PUBLIC)
                .isApproved(IsApproved.PENDING)
                .subscriptionCount(100).build();
        strategyRepository.save(strategy);

        //기본 승인 유무 확인
        assertEquals(strategy.getIsApproved(), IsApproved.PENDING);

        // 승인 거부로 변경
        adminStrategyService.manageAproveState(strategy.getStrategyId(), IsApproved.DENY);
        List<Strategy> strategies = strategyRepository.findAll();

        assertEquals(strategies.get(strategies.size()-1).getIsApproved(), IsApproved.DENY);

        //승인으로 변경
        adminStrategyService.manageAproveState(strategy.getStrategyId(), IsApproved.APPROVED);
        strategies = strategyRepository.findAll();
        assertEquals(strategies.get(strategies.size()-1).getIsApproved(), IsApproved.APPROVED);
    }
}
