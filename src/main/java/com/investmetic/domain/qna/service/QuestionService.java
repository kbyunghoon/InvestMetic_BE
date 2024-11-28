package com.investmetic.domain.qna.service;

import com.investmetic.domain.qna.dto.request.AdminQuestionsRequest;
import com.investmetic.domain.qna.dto.request.InvestorQuestionsRequest;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.request.TraderQuestionsRequest;
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

    // 투자자 문의 목록 조회
    public QuestionsPageResponse getInvestorQuestions(Long userId, Role userRole, InvestorQuestionsRequest request,
                                                      Pageable pageable) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        // 사용자 역할 확인
        if (!Role.isAdmin(userRole) && userRole != Role.INVESTOR) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 문의 검색
        Page<Question> questions = questionRepository.searchQuestions(
                userId,
                request.getKeyword(),
                request.getSearchCondition(),
                request.getStateCondition(),
                userRole,
                pageable,
                request.getStrategyName(),
                request.getTraderName(),
                null
        );

        // 결과 매핑 및 반환
        Page<QuestionsResponse> responsePage = questions.map(QuestionsResponse::from);
        return QuestionsPageResponse.from(new PageResponseDto<>(responsePage));
    }

    // 트레이더 문의 목록 조회
    public QuestionsPageResponse getTraderQuestions(Long userId, Role userRole, TraderQuestionsRequest request,
                                                    Pageable pageable) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        // 사용자 역할 확인
        if (userRole != Role.TRADER) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 문의 검색
        Page<Question> questions = questionRepository.searchQuestions(
                userId,
                request.getKeyword(),
                request.getSearchCondition(),
                request.getStateCondition(),
                userRole,
                pageable,
                request.getStrategyName(),
                null,
                request.getInvestorName()
        );

        // 결과 매핑 및 반환
        Page<QuestionsResponse> responsePage = questions.map(QuestionsResponse::from);
        return QuestionsPageResponse.from(new PageResponseDto<>(responsePage));
    }

    // 관리자 문의 목록 조회
    public QuestionsPageResponse getAdminQuestions(Long userId, Role userRole, AdminQuestionsRequest request,
                                                   Pageable pageable) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        // 관리자 여부 확인
        if (!Role.isAdmin(userRole)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 문의 검색
        Page<Question> questions = questionRepository.searchQuestions(
                userId,
                request.getKeyword(),
                request.getSearchCondition(),
                request.getStateCondition(),
                userRole,
                pageable,
                request.getStrategyName(),
                request.getTraderName(),
                request.getInvestorName()
        );

        // 결과 매핑 및 반환
        Page<QuestionsResponse> responsePage = questions.map(QuestionsResponse::from);
        return QuestionsPageResponse.from(new PageResponseDto<>(responsePage));
    }


    //문의 상세 조회
    // 투자자와 트레이더를 위한 문의 상세 조회
    public QuestionsDetailResponse getQuestionDetail(Long questionId, Long userId, Role role) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        Answer answer = question.getAnswer();

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

    // 관리자 전용 문의 상세 조회
    public QuestionsDetailResponse getAdminQuestionDetail(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        Answer answer = question.getAnswer(); // 답변 정보 포함
        return QuestionsDetailResponse.from(question, answer);
    }


}
