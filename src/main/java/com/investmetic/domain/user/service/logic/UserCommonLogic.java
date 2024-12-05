package com.investmetic.domain.user.service.logic;

import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.notice.repository.NoticeRepository;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.AnswerRepository;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.review.model.entity.Review;
import com.investmetic.domain.review.repository.ReviewRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.service.StrategyService;
import com.investmetic.domain.subscription.model.entity.Subscription;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommonLogic {

    private final StrategyRepository strategyRepository;
    private final StrategyService strategyService;
    private final SubscriptionRepository subscriptionRepository;
    private final ReviewRepository reviewRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    public void deleteUser(User user) {

        // 해당 회원이 구독한 모든 구독 삭제.
        deleteAllSubscription(user.getUserId());

        // 해당 회원이 남긴 모든 리뷰 삭제.
        deleteAllReview(user.getUserId());

        // 해당 회원이 남긴 문의 삭제.
        deleteAllQnA(user.getUserId());

        // 해당 회원이 관리자일 경우.
        if (Role.isAdmin(user.getRole())) {
            // 해당 관리자의 공지사항 작성자를 모두 SuperAdmin로 바꿈.
            changeNoticeUser(user.getUserId());
        }

        // 해당 유저가 투자자면 메서드 종료
        if (Role.isInvestor(user.getRole())) {
            return;
        }

        // 자신의 전략 모두 조회.
        List<Strategy> strategyList = strategyRepository.findAllByUserUserId(user.getUserId());

        // 자신의 전략이 있는지 확인.
        if (!strategyList.isEmpty()) {
            // 자신의 전략 하나씩 순회.
            for (Strategy strategy : strategyList) {
                // 일간, 월간, 통계, 리뷰, 구독, 문의, 투자그룹 삭제.
                strategyService.deleteStrategy(strategy.getStrategyId(), user.getUserId());
            }
        }
    }

    private void deleteAllQnA(Long userId) {
        // 문의 삭제
        List<Question> questionList = questionRepository.findAllByUserUserId(userId);
        List<Question> completeQustionList = new ArrayList<>();

        if (!questionList.isEmpty()) {
            for (Question question : questionList) {
                if (QnaState.COMPLETED.equals(question.getQnaState())) {
                    completeQustionList.add(question);
                }
            }
            //문의 먼저 삭제.
            answerRepository.deleteByQuestions(completeQustionList);
            questionRepository.deleteAllInBatch(questionList);
        }
    }


    private void deleteAllSubscription(Long userId) {
        // 해당 유저의 구독목록 가지고 오기.
        List<Subscription> userSubscriptionList = subscriptionRepository.findAllByUserUserId(userId);

        // 해당 회원이 전략을 구독 했는지 확인.
        if (!userSubscriptionList.isEmpty()) {
            // 해당 회원이 구독한 모든 전략의 구독 수 줄이기.
            for (Subscription subscription : userSubscriptionList) {
                Strategy strategy = strategyRepository.findById(subscription.getStrategy().getStrategyId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

                strategy.minusSubscriptionCount();
            }
            // 모든 구독 지우기.
            subscriptionRepository.deleteAllInBatch(userSubscriptionList);
        }
    }


    private void deleteAllReview(Long userId) {
        List<Review> userReviewList = reviewRepository.findAllByUserUserId(userId);

        // 자신이 남긴 모든 리뷰 지우기.
        if (!userReviewList.isEmpty()) {
            reviewRepository.deleteAllInBatch(userReviewList);
        }
    }


    private void changeNoticeUser(Long userId) {
        //공지사항 모두 SuperAdmin으로 넣기.
        List<Notice> noticeList = noticeRepository.findAllByUserUserId(userId);

        if (!noticeList.isEmpty()) {
            User superAdminUser = userRepository.findSuperAdminUser();

            for (Notice notice : noticeList) {
                notice.modifyUser(superAdminUser);
            }

            noticeRepository.saveAll(noticeList);
        }
    }


}
