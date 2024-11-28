package com.investmetic.domain.qna.service;

import static com.investmetic.domain.qna.model.entity.QQuestion.question;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import com.investmetic.domain.qna.dto.request.AdminQuestionsRequest;
import com.investmetic.domain.qna.dto.request.InvestorQuestionsRequest;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.request.TraderQuestionsRequest;
import com.investmetic.domain.qna.dto.response.QuestionsDetailResponse;
import com.investmetic.domain.qna.dto.response.QuestionsResponse;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final StrategyRepository strategyRepository;
    private final JPAQueryFactory queryFactory;

    /**
     * 문의 등록
     */
    @Transactional
    public void createQuestion(Long userId, Long strategyId, QuestionRequestDto questionRequestDto) {
        User user = findUserById(userId);
        Strategy strategy = findStrategyById(strategyId);
        Question question = Question.from(user, strategy, questionRequestDto);
        questionRepository.save(question);
    }

    /**
     * 문의 삭제
     */
    @Transactional
    public void deleteQuestion(Long strategyId, Long questionId, Long userId) {
        Strategy strategy = findStrategyById(strategyId);
        User user = findUserById(userId);
        Question question = findQuestionById(questionId);
        validateAccess(user, question, userId);
        questionRepository.delete(question);
    }

    /**
     * 투자자 문의 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<QuestionsResponse> getInvestorQuestions(Long userId, Role userRole, InvestorQuestionsRequest request,
                                                        Pageable pageable) {
        validateRole(userId, userRole, Role.INVESTOR);
        return searchQuestions(userId, request, pageable, Role.INVESTOR);
    }

    /**
     * 트레이더 문의 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<QuestionsResponse> getTraderQuestions(Long userId, Role userRole, TraderQuestionsRequest request,
                                                      Pageable pageable) {
        validateRole(userId, userRole, Role.TRADER);
        return searchQuestions(userId, request, pageable, Role.TRADER);
    }

    /**
     * 관리자 문의 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<QuestionsResponse> getAdminQuestions(Long userId, Role userRole, AdminQuestionsRequest request,
                                                     Pageable pageable) {
        validateAdmin(userRole);
        return searchQuestions(null, request, pageable, Role.SUPER_ADMIN);
    }

    /**
     * 문의 상세 조회 (투자자/트레이더)
     */
    @Transactional(readOnly = true)
    public QuestionsDetailResponse getQuestionDetail(Long questionId, Long userId, Role role) {
        Question question = findQuestionById(questionId);
        validateAccess(findUserById(userId), question, userId);
        return QuestionsDetailResponse.from(question, question.getAnswer());
    }

    /**
     * 관리자 전용 문의 상세 조회
     */
    @Transactional(readOnly = true)
    public QuestionsDetailResponse getAdminQuestionDetail(Long questionId, Role userRole) {
        validateAdmin(userRole);
        Question question = findQuestionById(questionId);
        return QuestionsDetailResponse.from(question, question.getAnswer());
    }

    // ================== Private Methods ==================

    /**
     * 사용자 조회 및 예외 처리 userId가 null인 경우 USERS_NOT_FOUND 예외를 던짐
     */
    private User findUserById(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USERS_NOT_FOUND);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));
    }

    /**
     * 전략 조회 및 예외 처리
     */
    private Strategy findStrategyById(Long strategyId) {
        return strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
    }

    /**
     * 질문 조회 및 예외 처리
     */
    private Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
    }

    /**
     * 역할 검증
     */
    private void validateRole(Long userId, Role userRole, Role requiredRole) {
        User user = findUserById(userId);
        if (!userRole.equals(requiredRole) && !Role.isAdmin(userRole)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    /**
     * 관리자 역할 검증
     */
    private void validateAdmin(Role role) {
        if (!Role.isAdmin(role)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    /**
     * 접근 권한 검증
     */
    private void validateAccess(User user, Question question, Long userId) {
        if (Role.isAdmin(user.getRole())) {
            return;
        }

        if (user.getRole() == Role.INVESTOR && !question.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        if (user.getRole() == Role.TRADER && !question.getStrategy().getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
    }

    /**
     * 문의 검색 및 반환
     */
    private Page<QuestionsResponse> searchQuestions(Long userId, QuestionRequestDto request, Pageable pageable,
                                                    Role role) {
        List<BooleanExpression> conditions = buildConditions(
                userId,
                request.getKeyword(),
                request.getSearchCondition(),
                request.getStateCondition(),
                role,
                request instanceof InvestorQuestionsRequest ? ((InvestorQuestionsRequest) request).getStrategyName()
                        : null,
                request instanceof AdminQuestionsRequest ? ((AdminQuestionsRequest) request).getTraderName() : null,
                request instanceof TraderQuestionsRequest ? ((TraderQuestionsRequest) request).getInvestorName() : null
        );

        Page<Question> questions = questionRepository.searchByConditions(conditions, pageable, queryFactory);
        return questions.map(QuestionsResponse::from);
    }

    /**
     * 검색 조건 빌드
     */
    private List<BooleanExpression> buildConditions(Long userId, String keyword, SearchCondition searchCondition,
                                                    StateCondition stateCondition, Role role, String strategyName,
                                                    String traderName, String investorName) {
        List<BooleanExpression> conditions = new ArrayList<>();

        if (role == Role.INVESTOR && userId != null) {
            conditions.add(question.user.userId.eq(userId));
        } else if (role == Role.TRADER && userId != null) {
            conditions.add(question.strategy.user.userId.eq(userId));
        } else if (role == Role.SUPER_ADMIN && userId != null) { // 관리자일 경우
            conditions.add(question.user.userId.eq(userId).or(question.strategy.user.userId.eq(userId)));
        }

        if (keyword != null && !keyword.isBlank()) {
            switch (searchCondition) {
                case TITLE:
                    conditions.add(question.title.containsIgnoreCase(keyword));
                    break;
                case CONTENT:
                    conditions.add(question.content.containsIgnoreCase(keyword));
                    break;
                case TITLE_OR_CONTENT:
                    conditions.add(
                            question.title.containsIgnoreCase(keyword)
                                    .or(question.content.containsIgnoreCase(keyword))
                    );
                    break;
                default:
                    break;
            }
        }

        if (stateCondition != null) {
            switch (stateCondition) {
                case WAITING:
                    conditions.add(question.qnaState.eq(QnaState.WAITING));
                    break;
                case COMPLETED:
                    conditions.add(question.qnaState.eq(QnaState.COMPLETED));
                    break;
                default:
                    break;
            }
        }

        if (strategyName != null && !strategyName.isBlank()) {
            conditions.add(question.strategy.strategyName.containsIgnoreCase(strategyName));
        }
        if (traderName != null && !traderName.isBlank()) {
            conditions.add(question.strategy.user.nickname.containsIgnoreCase(traderName));
        }
        if (investorName != null && !investorName.isBlank()) {
            conditions.add(question.user.nickname.containsIgnoreCase(investorName));
        }

        return conditions;
    }
}
