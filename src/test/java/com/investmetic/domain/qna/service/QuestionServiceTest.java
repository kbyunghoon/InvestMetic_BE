package com.investmetic.domain.qna.service;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QuestionServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private  StrategyRepository strategyRepository;

    @Autowired
    private TradeTypeRepository tradeTypeRepository;


    @Test
     void test() {
        TradeType tradeType = TestEntityFactory.createTestTradeType();
        User user = TestEntityFactory.createTestUser();
        Strategy testStrategy = TestEntityFactory.createTestStrategy(user,tradeType);
        userRepository.save(user);
        strategyRepository.save(testStrategy);
    }

}