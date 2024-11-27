package com.investmetic.domain.qna.service;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.response.QuestionsDetailResponse;
import com.investmetic.domain.qna.dto.response.QuestionsPageResponse;
import com.investmetic.domain.qna.dto.response.QuestionsResponse;
import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final StrategyRepository strategyRepository;

    //문의 생성
    @Transactional
    public void createQuestion(Long userId, Long strategyId, QuestionRequestDto questionRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        Question question = Question.from(user, strategy, questionRequestDto);
        questionRepository.save(question);
    }

    //문의 삭제
    @Transactional
    public void deleteQuestion(Long strategyId, Long questionId, Long userId) {

        strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        if (!Role.isAdmin(user.getRole())) {
            if (user.getRole() == Role.INVESTOR) {
                if (!question.getUser().getUserId().equals(userId)) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
                }
            } else if (user.getRole() == Role.TRADER) {
                if (!question.getStrategy().getUser().getUserId().equals(userId)) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
                }
            } else {
                throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
            }
        }

        questionRepository.delete(question);
    }

    //투자자 문의 목록 조회

    public QuestionsPageResponse getInvestorQuestions(Long userId, String keyword, SearchCondition searchCondition,
                                                      StateCondition stateCondition, String strategyName,
                                                      String traderName,
                                                      Pageable pageable, Role role) {
        // 사용자 존재 여부 확인

        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        if (!Role.isAdmin(role) && role != Role.INVESTOR) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS); // 투자자나 관리자가 아니면 예외
        }

        Page<Question> questions = questionRepository.searchQuestions(
                userId,
                keyword,
                searchCondition,
                stateCondition,
                role,
                pageable,
                strategyName,
                traderName,
                null
        );

        Page<QuestionsResponse> responsePage = questions.map(QuestionsResponse::from);
        return QuestionsPageResponse.from(new PageResponseDto<>(responsePage));
    }

    //트레이더 문의 목록 조회
    public QuestionsPageResponse getTraderQuestions(Long userId, String keyword, SearchCondition searchCondition,
                                                    StateCondition stateCondition, String investorName,
                                                    String strategyName,
                                                    Pageable pageable) {
        // 사용자 존재 여부 확인

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        Role role = userRepository.findById(userId)
                .map(User::getRole)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        Page<Question> questions = questionRepository.searchQuestions(
                userId,
                keyword,
                searchCondition,
                stateCondition,
                role,
                pageable,
                strategyName,
                null,
                investorName
        );

        Page<QuestionsResponse> responsePage = questions.map(QuestionsResponse::from);
        return QuestionsPageResponse.from(new PageResponseDto<>(responsePage));
    }

    //관리자 문의 목록 조회
    public QuestionsPageResponse getAdminQuestions(Long userId, String keyword, SearchCondition searchCondition,
                                                   StateCondition stateCondition, String investorName,
                                                   String strategyName, String traderName, Pageable pageable,
                                                   Role role) {
        // 관리자 여부 확인
        if (!Role.isAdmin(role)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS); // 관리자가 아니면 예외 처리
        }
        Page<Question> questions = questionRepository.searchQuestions(
                userId,
                keyword,
                searchCondition,
                stateCondition,
                role, // 실제 역할에 따라 관리자 역할 중 하나를 전달
                pageable,
                strategyName,
                traderName,
                investorName
        );

        Page<QuestionsResponse> responsePage = questions.map(QuestionsResponse::from);
        return QuestionsPageResponse.from(new PageResponseDto<>(responsePage));
    }


    //문의 상세 조회
    public QuestionsDetailResponse getQuestionDetail(Long questionId, Long userId, Role role) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        Answer answer = question.getAnswer();
        // 관리자 권한이면 모든 상세 정보를 볼 수 있음
        if (Role.isAdmin(role)) {
            return QuestionsDetailResponse.from(question, answer);
        }

        // 투자자 권한 확인
        if (role == Role.INVESTOR && userId != null) {
            if (!question.getUser().getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
            }
        }

        // 트레이더 권한 확인
        if (role == Role.TRADER && userId != null) {
            if (!question.getStrategy().getUser().getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
            }
        }

        return QuestionsDetailResponse.from(question, answer);
    }

}
