package com.investmetic.domain.qna.controller;

import com.investmetic.domain.qna.dto.request.AdminQuestionsRequest;
import com.investmetic.domain.qna.dto.request.InvestorQuestionsRequest;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.request.TraderQuestionsRequest;
import com.investmetic.domain.qna.dto.response.QuestionsDetailResponse;
import com.investmetic.domain.qna.dto.response.QuestionsPageResponse;
import com.investmetic.domain.qna.service.QuestionService;
import com.investmetic.domain.user.model.Role;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.exception.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;


    /**
     * 문의 등록
     *
     * @param strategyId         전략 ID
     * @param userId             사용자 ID
     * @param questionRequestDto 문의 내용 DTO
     * @return 문의 등록 성공 응답
     */
    @PostMapping("/strategies/{strategyId}/questions")
    public ResponseEntity<BaseResponse<Void>> createQuestion(
            @PathVariable Long strategyId,
            @RequestParam Long userId,
            @RequestBody @Valid QuestionRequestDto questionRequestDto) {

        questionService.createQuestion(userId, strategyId, questionRequestDto);
        return BaseResponse.success(SuccessCode.CREATED);
    }

    /**
     * 문의 삭제 (투자자, 트레이더 또는 관리자)
     *
     * @param strategyId 전략 ID
     * @param questionId 문의 ID
     * @param userId     사용자 ID
     * @return 문의 삭제 성공 응답
     */
    @DeleteMapping("/strategies/{strategyId}/questions/{questionId}")
    public ResponseEntity<BaseResponse<Void>> deleteQuestion(
            @PathVariable Long strategyId,
            @PathVariable Long questionId,
            @RequestParam Long userId) {

        questionService.deleteQuestion(strategyId, questionId, userId);
        return BaseResponse.success(SuccessCode.DELETED);
    }

    /**
     * 투자자 문의 목록 조회
     *
     * @param userId   사용자 ID
     * @param userRole 사용자 역할
     * @param request  투자자 요청 DTO
     * @param pageable 페이징 정보
     * @return 투자자 문의 목록
     */
    @PostMapping("/investor/{userId}/questions")
    public ResponseEntity<BaseResponse<QuestionsPageResponse>> getInvestorQuestions(
            @PathVariable Long userId,
            @RequestParam Role userRole,// 추후 Security 적용 시 제거 예정
            @RequestBody @Valid InvestorQuestionsRequest request,
            @PageableDefault(size = 4, sort = "createdAt") Pageable pageable) {

        // 추후 Security 적용 시 Authentication 객체로부터 userId와 userRole을 추출할 예정
        QuestionsPageResponse response = questionService.getInvestorQuestions(userId, userRole, request, pageable);
        return BaseResponse.success(response);
    }

    /**
     * 트레이더 문의 목록 조회
     *
     * @param userId   사용자 ID
     * @param userRole 사용자 역할
     * @param request  트레이더 요청 DTO
     * @param pageable 페이징 정보
     * @return 트레이더 문의 목록
     */
    @PostMapping("/trader/{userId}/questions")
    public ResponseEntity<BaseResponse<QuestionsPageResponse>> getTraderQuestions(
            @PathVariable Long userId,
            @RequestParam Role userRole,// 추후 Security 적용 시 제거 예정
            @RequestBody @Valid TraderQuestionsRequest request,
            @PageableDefault(size = 4, sort = "createdAt") Pageable pageable) {

        // 추후 Security 적용 시 Authentication 객체로부터 userId와 userRole을 추출할 예정
        QuestionsPageResponse response = questionService.getTraderQuestions(userId, userRole, request, pageable);
        return BaseResponse.success(response);
    }

    /**
     * 관리자 문의 목록 조회
     *
     * @param userId   사용자 ID
     * @param userRole 사용자 역할
     * @param request  관리자 요청 DTO
     * @param pageable 페이징 정보
     * @return 관리자 문의 목록
     */
    @PostMapping("/admin/{userId}/questions")
    public ResponseEntity<BaseResponse<QuestionsPageResponse>> getAdminQuestions(
            @PathVariable Long userId,
            @RequestParam Role userRole,// 추후 Security 적용 시 제거 예정
            @RequestBody @Valid AdminQuestionsRequest request,
            @PageableDefault(size = 8, sort = "createdAt") Pageable pageable) {

        // 추후 Security 적용 시 Authentication 객체로부터 userId와 userRole을 추출할 예정
        QuestionsPageResponse response = questionService.getAdminQuestions(userId, userRole, request, pageable);
        return BaseResponse.success(response);
    }

    /**
     * 문의 상세 조회 (투자자)
     *
     * @param questionId 문의 ID
     * @param userId     사용자 ID
     * @return 문의 상세 정보 응답
     */
    @GetMapping("/investor/questions/{questionId}")
    public ResponseEntity<BaseResponse<QuestionsDetailResponse>> getInvestorQuestionDetail(
            @PathVariable Long questionId,
            @RequestParam Long userId) {

        QuestionsDetailResponse response = questionService.getQuestionDetail(questionId, userId, Role.INVESTOR);
        return BaseResponse.success(response);
    }

    /**
     * 문의 상세 조회 (트레이더)
     *
     * @param questionId 문의 ID
     * @param userId     사용자 ID
     * @return 문의 상세 정보 응답
     */
    @GetMapping("/trader/questions/{questionId}")
    public ResponseEntity<BaseResponse<QuestionsDetailResponse>> getTraderQuestionDetail(
            @PathVariable Long questionId,
            @RequestParam Long userId) {

        QuestionsDetailResponse response = questionService.getQuestionDetail(questionId, userId, Role.TRADER);
        return BaseResponse.success(response);
    }

    @GetMapping("/admin/questions/{questionId}")
    public ResponseEntity<BaseResponse<QuestionsDetailResponse>> getAdminQuestionDetail(
            @PathVariable Long questionId,
            @RequestParam Role userRole) {// 추후 Security 적용 시 제거 예정

        // 추후 Security 적용 시 Authentication 객체로부터 userId와 userRole을 추출할 예정
        if (userRole == null || !Role.isAdmin(userRole)) { // 유효성 검사 추가
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS); // 관리자가 아니면 접근 불가
        }

        QuestionsDetailResponse response = questionService.getQuestionDetail(questionId, null, userRole);
        return BaseResponse.success(response);
    }
}
