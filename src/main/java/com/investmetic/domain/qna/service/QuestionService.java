package com.investmetic.domain.qna.service;

import com.investmetic.domain.qna.dto.request.AdminQuestionListRequestDto;
import com.investmetic.domain.qna.dto.request.InvestorQuestionListRequestDto;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.request.TraderQuestionListRequestDto;
import com.investmetic.domain.qna.dto.response.AdminQuestionListResponseDto;
import com.investmetic.domain.qna.dto.response.InvestorQuestionListResponseDto;
import com.investmetic.domain.qna.dto.response.QuestionDetailResponseDto;
import com.investmetic.domain.qna.dto.response.TraderQuestionListResponseDto;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final StrategyRepository strategyRepository;

    //문의 등록
    @Transactional
    public void createQuestion(Long userId, Long strategyId, QuestionRequestDto questionRequestDto) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        Question question = questionRequestDto.toEntity(user, strategy);
        questionRepository.save(question);
    }

    //문의 삭제
    @Transactional
    public void deleteQuestion(Long strategyId, Long questionId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
        Question question = questionRepository.findByStrategyAndQuestionId(strategy, questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        questionRepository.delete(question);
    }

    //투자자 문의 목록 조회
    public ResponseEntity<BaseResponse<PageResponseDto<InvestorQuestionListResponseDto>>> getInvestorQuestionList(
            Long userId, InvestorQuestionListRequestDto requestDto, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        Sort sort = Sort.by(requestDto.getSortBy()).descending();
        if ("ASC".equalsIgnoreCase(requestDto.getSort())) {
            sort = Sort.by(requestDto.getSortBy()).ascending();
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Question> questions = questionRepository.findQuestionsForInvestor(
                requestDto.getKeyword(),
                requestDto.getQnaState(),
                user,
                sortedPageable
        );

        Page<InvestorQuestionListResponseDto> response = questions.map(InvestorQuestionListResponseDto::from);

        return BaseResponse.success(new PageResponseDto<>(response));
    }

    //트레이더 문의 목록 조회
    public ResponseEntity<BaseResponse<PageResponseDto<TraderQuestionListResponseDto>>> getTraderQuestionsList(
            Long strategyId, TraderQuestionListRequestDto requestDto, Pageable pageable) {

        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        Sort sort = Sort.by(requestDto.getSortBy()).descending();
        if ("ASC".equalsIgnoreCase(requestDto.getSort())) {
            sort = Sort.by(requestDto.getSortBy()).ascending();
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Question> questions = questionRepository.findQuestionsForTrader(
                requestDto.getKeyword(),
                requestDto.getQnaState(),
                strategy,
                sortedPageable
        );

        Page<TraderQuestionListResponseDto> response = questions.map(TraderQuestionListResponseDto::from);

        return BaseResponse.success(new PageResponseDto<>(response));
    }

    // 관리자 문의 목록 조회
    public ResponseEntity<BaseResponse<PageResponseDto<AdminQuestionListResponseDto>>> getAdminQuestionList(
            AdminQuestionListRequestDto requestDto, Pageable pageable) {

        Sort sort = Sort.by(requestDto.getSortBy()).descending();
        if ("ASC".equalsIgnoreCase(requestDto.getSort())) {
            sort = Sort.by(requestDto.getSortBy()).ascending();
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Question> questions = questionRepository.findQuestionsForAdmin(
                requestDto.getKeyword(),
                requestDto.getQnaState(),
                sortedPageable
        );

        Page<AdminQuestionListResponseDto> response = questions.map(AdminQuestionListResponseDto::from);

        return BaseResponse.success(new PageResponseDto<>(response));
    }
    // 투자자 문의 상세 조회
    public ResponseEntity<BaseResponse<QuestionDetailResponseDto>> getInvestorQuestionDetail(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        // 본인 문의인지 검증
        if (!question.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        return createResponse(question);
    }

    // 트레이더 문의 상세 조회
    public ResponseEntity<BaseResponse<QuestionDetailResponseDto>> getTraderQuestionDetail(Long questionId, Long traderId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        // 본인 전략의 질문인지 검증
        if (!question.getStrategy().getUser().getUserId().equals(traderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        return createResponse(question);
    }

    // 관리자 문의 상세 조회
    public ResponseEntity<BaseResponse<QuestionDetailResponseDto>> getAdminQuestionDetail(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        return createResponse(question);
    }

    // 공통 응답 생성 메서드
    private ResponseEntity<BaseResponse<QuestionDetailResponseDto>> createResponse(Question question) {
        QuestionDetailResponseDto response = QuestionDetailResponseDto.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .content(question.getContent())
                .investorName(question.getUser().getNickname())
                .traderName(question.getStrategy().getUser().getNickname())
                .strategyName(question.getStrategy().getStrategyName())
                .qnaState(question.getQnaState())
                .createdAt(question.getCreatedAt())
                .answerId(null) // 답변 연결 시 추가
                .build();

        return BaseResponse.success(response);
    }
}
