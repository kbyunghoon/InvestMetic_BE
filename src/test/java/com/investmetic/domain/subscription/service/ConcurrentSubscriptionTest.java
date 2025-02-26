//package com.investmetic.domain.subscription.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.investmetic.domain.TestEntity.TestEntityFactory;
//import com.investmetic.domain.strategy.model.entity.Strategy;
//import com.investmetic.domain.strategy.model.entity.TradeType;
//import com.investmetic.domain.strategy.repository.StrategyRepository;
//import com.investmetic.domain.strategy.repository.TradeTypeRepository;
//import com.investmetic.domain.user.model.entity.User;
//import com.investmetic.domain.user.repository.UserRepository;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
///***
// * 멀티스레드 동시성 테스트에는 @Transactional을 붙이면 오류남
// * 그래서 해당테스트는 반드시 테스트DB에 돌려야함.
// */
//@SpringBootTest
//public class ConcurrentSubscriptionTest {
//    @Autowired
//    private SubscriptionService subscriptionService;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private StrategyRepository strategyRepository;
//    @Autowired
//    private TradeTypeRepository tradeTypeRepository;
//
//    private Strategy testStrategy;
//    private User testUser;
//    private TradeType testTradeType;
//    private static final Integer parallelism = 5;
//    private final ExecutorService executorService = Executors.newFixedThreadPool(parallelism);
//
//    @BeforeEach
//    public void setup() {
//        testUser = userRepository.saveAndFlush(TestEntityFactory.createTestUser("testUser", "testuser@example.com"));
//        testTradeType = tradeTypeRepository.saveAndFlush(TestEntityFactory.createTestTradeType());
//        testStrategy = strategyRepository.saveAndFlush(TestEntityFactory.createTestStrategy(testUser, testTradeType));
//    }
//
//
//    @DisplayName("구독수 동시성 테스트")
//    @Test
//    void 구독수_동시성_테스트() throws InterruptedException {
//
//        // 테스트 시작 시의 초기 구독 수를 저장
//        int initialSubscriptionCount = testStrategy.getSubscriptionCount();
//        int concurrentRequests = 100;
//        CountDownLatch latch = new CountDownLatch(concurrentRequests);
//        // 100명의 신규 사용자를 생성한 후, 각각이 동일 전략에 대해 구독 요청을 보낸다.
//        for (int i = 0; i < concurrentRequests; i++) {
//            // TestEntityFactory를 통해 고유한 사용자 생성
//            User testUser = TestEntityFactory.createTestUser("user" + i, "email" + i);
//            userRepository.saveAndFlush(testUser);
//
//            // 각 스레드에서 구독 요청 실행
//            executorService.submit(() -> {
//                try {
//                    subscriptionService.subscribe(testStrategy.getStrategyId(), testUser.getUserId());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        // 모든 스레드가 완료될 때까지 대기
//        latch.await();
//
//        // 최신 전략 정보를 조회하여 구독 수 확인
//        Strategy updatedStrategy = strategyRepository.findById(testStrategy.getStrategyId())
//                .orElseThrow(() -> new RuntimeException("전략을 찾을 수 없습니다."));
//        int finalSubscriptionCount = updatedStrategy.getSubscriptionCount();
//
//        // 100명의 신규 사용자가 구독 요청을 했으므로, 최종 구독 수는 초기 구독 수보다 100이 증가해야 함
//        assertThat(finalSubscriptionCount).isEqualTo(initialSubscriptionCount + concurrentRequests);
//    }
//}
