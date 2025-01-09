package com.investmetic.domain.qna.service;

import static com.investmetic.domain.qna.model.entity.QQuestion.question;

import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.response.AnswerResponseDto;
import com.investmetic.domain.qna.dto.response.QuestionsDetailResponse;
import com.investmetic.domain.qna.dto.response.QuestionsResponse;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.AnswerRepository;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
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
    private final AnswerRepository answerRepository;

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
        Question question = findQuestionById(questionId);
        User user = findUserById(userId);

        validateAccess(user, question, userId);

        answerRepository.findByQuestion(question).ifPresent(answerRepository::delete);

        questionRepository.delete(question);
    }

    /**
     * 관리자 문의 삭제
     */
    @Transactional
    public void adminDeleteQuestion(Long strategyId, Long questionId, Long userId) {
        Question question = findQuestionById(questionId);
        User user = findUserById(userId);

        validateAdminAccess(user);

        answerRepository.findByQuestion(question).ifPresent(answerRepository::delete);

        questionRepository.delete(question);
    }

    /**
     * 투자자 문의 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponseDto<QuestionsResponse> getInvestorQuestions(Long userId, Role userRole,
                                                                   String keyword,
                                                                   SearchCondition searchCondition,
                                                                   StateCondition stateCondition,
                                                                   Pageable pageable) {
        return searchQuestions(userId, keyword, searchCondition, stateCondition, pageable, userRole);
    }

    /**
     * 트레이더 문의 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponseDto<QuestionsResponse> getTraderQuestions(Long userId, Role userRole,
                                                                 String keyword,
                                                                 SearchCondition searchCondition,
                                                                 StateCondition stateCondition,
                                                                 Pageable pageable) {
        // userRole에 TRADER가 들어감.
        return searchQuestions(userId, keyword, searchCondition, stateCondition, pageable, userRole);
    }

    /**
     * 관리자 문의 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponseDto<QuestionsResponse> getAdminQuestions(Long userId, Role userRole,
                                                                String keyword,
                                                                SearchCondition searchCondition,
                                                                StateCondition stateCondition,
                                                                Pageable pageable) {
        // 관리자 조회는 userId를 null로 전달하여 모든 데이터를 조회
        return searchQuestions(null, keyword, searchCondition, stateCondition, pageable, userRole);
    }

    /**
     * 문의 상세 조회 (투자자/트레이더)
     */
    @Transactional(readOnly = true)
    public QuestionsDetailResponse getQuestionDetail(Long questionId, Long userId, Role role) {
        Question question = findQuestionById(questionId);
        User user = findUserById(userId);

        // 접근 권한 검증
        validateAccess(user, question, userId);

        // DTO 생성
        return createQuestionsDetailResponse(question, role);
    }

    /**
     * 관리자 전용 문의 상세 조회
     */
    @Transactional(readOnly = true)
    public QuestionsDetailResponse getAdminQuestionDetail(Long questionId, Role userRole) {
        // 문의 데이터 조회
        Question question = findQuestionById(questionId);

        // DTO 생성
        return createQuestionsDetailResponse(question, userRole);
    }

    /**
     * QuestionsDetailResponse 생성
     */
    private QuestionsDetailResponse createQuestionsDetailResponse(Question question, Role role) {
        Answer answer = answerRepository.findByQuestion(question).orElse(null);
        User trader = null;

        if (answer != null) {
            trader = answer.getUser(); // 답변이 있을 경우만 트레이더 정보 설정
        }

        // 역할에 따른 정보를 설정
        String profileImageUrl = "http://default-image-url.com/default.jpg"; // 기본 이미지 URL
        String nickname;

        if (Role.isInvestor(role)) {
            // 투자자: 트레이더 정보 반환
            profileImageUrl = question.getUser().getImageUrl(); // 트레이더 이미지
            nickname = question.getUser().getNickname();       // 트레이더 닉네임
        } else if (Role.isTrader(role)) {
            // 트레이더: 투자자 정보 반환
            profileImageUrl = question.getStrategy().getUser().getImageUrl();              // 투자자 이미지
            nickname = question.getStrategy().getUser().getNickname();                    // 투자자 닉네임
        } else {
            // 관리자: 기본 정보 반환
            profileImageUrl = question.getUser().getImageUrl();              // 투자자 이미지
            nickname = question.getUser().getNickname();                    // 투자자 닉네임
        }

        // 답변이 없는 경우에는 Answer 관련 필드를 적절히 처리
        AnswerResponseDto answerResponse = null;
        if (answer != null) {
            answerResponse = AnswerResponseDto.builder()
                    .answerId(answer.getAnswerId())
                    .content(answer.getContent())
                    .role(trader != null ? trader.getRole() : null)
                    .profileImageUrl(trader != null ? trader.getImageUrl() : null)
                    .nickname(trader != null ? trader.getNickname() : "Unknown") // 트레이더가 없을 경우 기본값
                    .createdAt(answer.getCreatedAt())
                    .build();
        }

        return QuestionsDetailResponse.builder()
                .questionId(question.getQuestionId())
                .strategyId(question.getStrategy().getStrategyId())
                .title(question.getTitle())
                .content(question.getContent())
                .strategyName(question.getStrategy() != null ? question.getStrategy().getStrategyName() : "전략 없음")
                .profileImageUrl(profileImageUrl)
                .nickname(nickname)
                .state(question.getQnaState().name())
                .createdAt(question.getCreatedAt())
                .answer(answerResponse) // 답변이 없는 경우 null 처리
                .build();
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
     * 접근 권한 검증
     */
    private void validateAccess(User user, Question question, Long userId) {
        switch (user.getRole()) {
            case INVESTOR:
                validateInvestorAccess(user, question, userId);
                break;
            case TRADER:
                validateTraderAccess(user, question, userId);
                break;
            case INVESTOR_ADMIN:
            case TRADER_ADMIN:
            case SUPER_ADMIN:
                validateAdminAccess(user);
                break;
            default:
                throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    /**
     * 투자자 접근 권한 검증
     */
    private void validateInvestorAccess(User user, Question question, Long userId) {
        if (!question.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    /**
     * 트레이더 접근 권한 검증
     */
    private void validateTraderAccess(User user, Question question, Long userId) {
        if (!question.getStrategy().getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    /**
     * 관리자 접근 권한 검증
     */
    private void validateAdminAccess(User user) {
        if (!Role.isAdmin(user.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }


    public PageResponseDto<QuestionsResponse> searchQuestions(Long userId, String keyword,
                                                              SearchCondition searchCondition,
                                                              StateCondition stateCondition,
                                                              Pageable pageable, Role role) {
        // 검색 조건 생성
        List<BooleanExpression> conditions = buildConditions(
                userId,
                keyword,
                searchCondition,
                stateCondition,
                role
        );

        // DTO 변환
        Page<Question> questions = questionRepository.searchByConditions(conditions, pageable, queryFactory);
        return new PageResponseDto<>(questions.map(q ->
                filterQuestions(q, role)
        ));
    }

    private QuestionsResponse filterQuestions(Question question, Role role) {
        return switch (role) {
            case TRADER -> filterTraderQuestions(question);
            case INVESTOR -> filterInvestorQuestions(question);
            default -> {
                if (isAdminRole(role)) {
                    yield filterAdminQuestions(question);
                }
                throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
            }
        };
    }

    private boolean isAdminRole(Role role) {
        return role == Role.INVESTOR_ADMIN || role == Role.TRADER_ADMIN || role == Role.SUPER_ADMIN;
    }

    private QuestionsResponse filterInvestorQuestions(Question question) {
        Answer answer = answerRepository.findByQuestion(question).orElse(null);

        if (answer != null) {
            User trader = answer.getUser();

            return QuestionsResponse.forAdmin(question, trader);
        } else {
            return QuestionsResponse.forInvestor(question, question.getStrategy().getUser());
        }
    }

    private QuestionsResponse filterTraderQuestions(Question question) {
        return QuestionsResponse.forTrader(question);
    }

    private QuestionsResponse filterAdminQuestions(Question question) {
        Answer answer = answerRepository.findByQuestion(question).orElse(null);

        if (answer != null) {
            User trader = answer.getUser();
            return QuestionsResponse.forAdmin(question, trader);
        } else {
            User trader = question.getStrategy().getUser();
            return QuestionsResponse.forAdmin(question, trader);
        }
    }


    /**
     * 검색 조건 빌드
     */
    private List<BooleanExpression> buildConditions(Long userId, String keyword, SearchCondition searchCondition,
                                                    StateCondition stateCondition, Role role) {
        List<BooleanExpression> conditions = new ArrayList<>();

        // 역할에 따른 기본 조건 추가
        if (Role.isInvestor(role) && userId != null) {
            // 투자자 또는 투자자 관리자
            conditions.add(question.user.userId.eq(userId));
        } else if (Role.isTrader(role) && userId != null) {
            // 트레이더 또는 트레이더 관리자
            // 해당 전략의 userId 가 현재 요청한 유저의 userId와 같은 것만 표시.
            conditions.add(question.strategy.user.userId.eq(userId));
        } else if (Role.isAdmin(role)) {
            // 관리자 (슈퍼 관리자, 투자자 관리자, 트레이더 관리자)
        }

        // 키워드 검색 조건 추가
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
                case STRATEGY_NAME:
                    conditions.add(question.strategy.strategyName.containsIgnoreCase(keyword));
                    break;
                case INVESTOR_NAME:
                    if (Role.isTrader(role) || Role.isAdmin(role)) {
                        conditions.add(question.user.nickname.containsIgnoreCase(keyword));
                    }
                    break;
                case TRADER_NAME:
                    if (Role.isInvestor(role) || Role.isAdmin(role)) {
                        conditions.add(question.strategy.user.nickname.containsIgnoreCase(keyword));
                    }
                    break;
                default:
                    break;
            }
        }

        // 상태 조건 추가
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

        return conditions;
    }

}
