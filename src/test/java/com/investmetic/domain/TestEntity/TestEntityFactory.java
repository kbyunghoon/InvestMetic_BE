package com.investmetic.domain.TestEntity;

import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import java.time.LocalDate;

public class TestEntityFactory {

    public static User createTestUser() {
        return User.builder()
                .userName("testUser")
                .nickname("Test Nickname")
                .email("testuser@example.com")
                .password("encryptedPassword")
                .imageUrl("http://example.com/image.jpg")
                .phone("123-456-7890")
                .birthDate("19900101")
                .ipAddress("192.168.0.1")
                .infoAgreement(true)
                .joinDate(LocalDate.now())
                .withdrawalDate(null)
                .userState(UserState.ACTIVE)
                .withdrawalStatus(false)
                .role(Role.INVESTOR)
                .build();
    }

    public static TradeType createTestTradeType() {
        return TradeType.builder()
                .tradeName("Test Trade")
                .activate_state(true)
                .tradeIconPath("http://~~")
                .build();
    }

    public static Strategy createTestStrategy(User user, TradeType tradeType) {
        return Strategy.builder()
                .user(user)
                .tradeType(tradeType)
                .strategyName("매매 전략")
                .operationCycle(OperationCycle.DAY)
                .minimumInvestmentAmount(MinimumInvestmentAmount.UNDER_10K)
                .strategyDescription("전략상세")
                .proposalFilePath("http://~")
                .isPublic(IsPublic.PUBLIC)
                .isApproved(IsApproved.APPROVED)
                .subscriptionCount(100)
                .averageRating(0.0)
                .build();
    }
}
