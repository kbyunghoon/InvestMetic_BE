package com.investmetic.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.notice.dto.request.NoticeRegisterDto;
import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.notice.repository.NoticeRepository;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.AnswerRepository;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.review.repository.ReviewRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.strategy.service.StrategyListingService;
import com.investmetic.domain.subscription.service.SubscriptionService;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.domain.user.service.logic.UserCommonLogic;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
    private TradeTypeRepository tradeTypeRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private EntityManager em;

    private final String investorEmail = "TTEESSTTinvestor@exmaple.com";
    private final String investorNickname = "TTEESSTTinvestor";

    private final String traderEmail = "TTEESSTTtrader@exmaple.com";
    private final String traderNickname = "TTEESSTTtrader";

    private final String adminEmail = "TTEESSTTadmin@exmaple.com";
    private final String adminNickname = "TTEESSTTadmin";

    private Question question;



    @BeforeEach
    void setUp() {
        User investor = User.builder()
                .userName("test")
                .email(investorEmail)
                .nickname(investorNickname)
                .role(Role.INVESTOR)
                .build();

        User trader = User.builder()
                .userName("test")
                .email(traderEmail)
                .nickname(traderNickname)
                .role(Role.TRADER)
                .build();

        User admin = User.builder()
                .userName("test")
                .email(adminEmail)
                .nickname(adminNickname)
                .role(Role.TRADER_ADMIN)
                .build();

        User superAdmin = User.builder()
                .userName("test")
                .email("superadminEmail")
                .nickname("adminNickname")
                .role(Role.SUPER_ADMIN)
                .build();

        userRepository.save(investor);
        userRepository.save(trader);
        userRepository.save(admin);
        userRepository.save(superAdmin);

        TradeType tradeType = TestEntityFactory.createTestTradeType();

        Strategy strategy = TestEntityFactory.createTestStrategy(trader, tradeType);

        QuestionRequestDto dto = QuestionRequestDto.builder().title("test").content("asdf").build();
        tradeTypeRepository.save(tradeType);
        strategy= strategyRepository.save(strategy);

        // 구독 생성.
        subscriptionService.subscribe(strategy.getStrategyId(),investor.getUserId());

        // 문의 생성.
        question = Question.from(investor, strategy, dto);
        question.updateQnaState(QnaState.COMPLETED);
        question = questionRepository.save(question);

        // 답변 생성.
        Answer answer = Answer.builder()
                .user(trader)
                .question(question)
                .content("Answer Test")
                .build();

        answerRepository.save(answer);

        em.flush();
        em.clear();
    }



    @Test
    @DisplayName("회원 Trader 삭제 확인")
    void deleteUserTest1() {

        // given
        User trader = userRepository.findByEmail(traderEmail).orElse(null);
        assertThat(trader).isNotNull();

        em.flush();
        em.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        userCommonLogic.deleteUser(trader);
        em.flush();
        em.clear();

        // then
        // 해당 트레이더의 전략이 없어야함.
        assertThat(strategyListingService.getMyStrategies(trader.getUserId(), pageable).getContent())
                .isEmpty();

        //  strategyService.deleteStrategy()에서 test 검증 했으므로 일간, 월간 분석 Data등은 통과.

        // 해당 트레이더가 남긴 문의 답변이 없어야함.
        Optional<Answer> answer = answerRepository.findByQuestion(question);
        assertThat(answer).isNotPresent();
    }

    @Test
    @DisplayName("회원 Investor 삭제 확인")
    void deleteUserTest2() {

        User investor = userRepository.findByEmail(investorEmail).orElse(null);
        assertThat(investor).isNotNull();

        em.flush();
        em.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // 해당 회원이 구독한 전략이 있음을 확인.
        assertThat(strategyListingService.getSubscribedStrategies(investor.getUserId(), pageable).getContent())
                .isNotEmpty();

        // 해당 회원이 남긴 문의가 있음을 확인.
        assertThat(questionRepository.findAllByUserUserId(investor.getUserId())).isNotEmpty();

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

    @Test
    @DisplayName("Admin 회원 공지사항 있을경우.")
    void deleteUserTest3() {

        User admin = userRepository.findByEmail(adminEmail).orElse(null);
        assertThat(admin).isNotNull();

        Notice notice = NoticeRegisterDto.builder()
                .content("content")
                .title("title")
                .build()
                .toEntity(admin);

        // 공지사항 생성
        notice = noticeRepository.save(notice);

        assertThat(noticeRepository.findAllByUserUserId(admin.getUserId())).isNotEmpty();


        // when
        userCommonLogic.deleteUser(admin);
        em.flush();
        em.clear();

        // then
        assertThat(noticeRepository.findAllByUserUserId(admin.getUserId())).isEmpty();

        Optional<Notice> changedNotice = noticeRepository.findById(notice.getNoticeId());
        assertThat(changedNotice).isPresent();
        assertThat(changedNotice.get().getUser().getRole()).isEqualTo(Role.SUPER_ADMIN);

    }


}
