package com.investmetic.domain.qna.service;

import com.investmetic.domain.qna.dto.request.AdminQuestionsRequest;
import com.investmetic.domain.qna.dto.request.InvestorQuestionsRequest;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.request.TraderQuestionsRequest;
import com.investmetic.domain.qna.dto.response.QuestionsDetailResponse;
import com.investmetic.domain.qna.dto.response.QuestionsResponse;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
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

    /**
     * 문의 등록
     *
     * @param userId             사용자 ID
     * @param strategyId         전략 ID
     * @param questionRequestDto 문의 등록 요청 DTO
     */
    @Transactional
    public void createQuestion(Long userId, Long strategyId, QuestionRequestDto questionRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND)); // 사용자 존재 여부 확인

        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND)); // 전략 존재 여부 확인

        Question question = Question.from(user, strategy, questionRequestDto); // 새로운 문의 생성
        questionRepository.save(question); // 문의 저장
    }

    /**
     * 문의 삭제
     *
     * @param strategyId 전략 ID
     * @param questionId 문의 ID
     * @param userId     사용자 ID
     */
    @Transactional
    public void deleteQuestion(Long strategyId, Long questionId, Long userId) {
        strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND)); // 전략 존재 여부 확인

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND)); // 사용자 존재 여부 확인

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND)); // 문의 존재 여부 확인

        // 사용자 권한에 따른 삭제 권한 확인
        if (!Role.isAdmin(user.getRole())) {
            if (user.getRole() == Role.INVESTOR && !question.getUser().getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
            }
            if (user.getRole() == Role.TRADER && !question.getStrategy().getUser().getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
            }
        }

        questionRepository.delete(question); // 문의 삭제
    }

    /**
     * 투자자 문의 목록 조회
     *
     * @param userId   사용자 ID
     * @param userRole 사용자 역할
     * @param request  투자자 요청 DTO
     * @param pageable 페이징 정보
     * @return 문의 목록 (투자자 기준)
     */
    public Page<QuestionsResponse> getInvestorQuestions(Long userId, Role userRole, InvestorQuestionsRequest request,
                                                        Pageable pageable) {
        validateUser(userId, userRole, Role.INVESTOR); // 사용자 검증

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

        return questions.map(QuestionsResponse::from); // DTO 변환 후 반환
    }

    /**
     * 트레이더 문의 목록 조회
     *
     * @param userId   사용자 ID
     * @param userRole 사용자 역할
     * @param request  트레이더 요청 DTO
     * @param pageable 페이징 정보
     * @return 문의 목록 (트레이더 기준)
     */
    public Page<QuestionsResponse> getTraderQuestions(Long userId, Role userRole, TraderQuestionsRequest request,
                                                      Pageable pageable) {
        validateUser(userId, userRole, Role.TRADER); // 사용자 검증

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

        return questions.map(QuestionsResponse::from); // DTO 변환 후 반환
    }

    /**
     * 관리자 문의 목록 조회
     *
     * @param userId   사용자 ID
     * @param userRole 사용자 역할
     * @param request  관리자 요청 DTO
     * @param pageable 페이징 정보
     * @return 문의 목록 (관리자 기준)
     */
    public Page<QuestionsResponse> getAdminQuestions(Long userId, Role userRole, AdminQuestionsRequest request,
                                                     Pageable pageable) {
        validateUser(userId, userRole, Role.SUPER_ADMIN); // 관리자 여부 검증

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

        return questions.map(QuestionsResponse::from); // DTO 변환 후 반환
    }

    /**
     * 문의 상세 조회 (투자자 및 트레이더)
     *
     * @param questionId 문의 ID
     * @param userId     사용자 ID
     * @param role       사용자 역할
     * @return 문의 상세 정보
     */
    public QuestionsDetailResponse getQuestionDetail(Long questionId, Long userId, Role role) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND)); // 문의 존재 여부 확인

        if (role == Role.INVESTOR && !question.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS); // 투자자 권한 확인
        }
        if (role == Role.TRADER && !question.getStrategy().getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS); // 트레이더 권한 확인
        }

        return QuestionsDetailResponse.from(question, question.getAnswer()); // DTO 변환 후 반환
    }

    /**
     * 관리자 전용 문의 상세 조회
     *
     * @param questionId 문의 ID
     * @return 문의 상세 정보
     */
    public QuestionsDetailResponse getAdminQuestionDetail(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND)); // 문의 존재 여부 확인

        return QuestionsDetailResponse.from(question, question.getAnswer()); // DTO 변환 후 반환
    }

    /**
     * 사용자 검증 메서드
     *
     * @param userId      사용자 ID
     * @param userRole    사용자 역할
     * @param requiredRole 필요 역할
     */
    private void validateUser(Long userId, Role userRole, Role requiredRole) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND)); // 사용자 존재 여부 확인

        if (!Role.isAdmin(userRole) && userRole != requiredRole) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS); // 권한 확인
        }
    }
}
