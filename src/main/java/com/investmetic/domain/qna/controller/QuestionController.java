package com.investmetic.domain.qna.controller;


import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.response.QuestionsDetailResponse;
import com.investmetic.domain.qna.dto.response.QuestionsResponse;
import com.investmetic.domain.qna.service.QuestionService;
import com.investmetic.domain.user.model.Role;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
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

/**
 * QuestionController 문의 관련 API를 처리하는 컨트롤러 클래스 역할(투자자, 트레이더, 관리자)에 따라 접근 권한을 구분하여 처리
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 문의 등록
     *
     * @param strategyId         등록할 문의가 속한 전략 ID
     * @param userId             문의를 작성한 사용자 ID (추후 시큐리티 적용 시 Authentication에서 추출 예정)
     * @param questionRequestDto 문의 제목 및 내용 등의 요청 데이터
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
     * 문의 삭제
     *
     * @param strategyId 삭제할 문의가 속한 전략 ID
     * @param questionId 삭제할 문의 ID
     * @param userId     삭제 요청을 한 사용자 ID (추후 시큐리티 적용 시 Authentication에서 추출 예정)
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
     * @param userId   투자자 ID (추후 시큐리티 적용 시 Authentication에서 추출 예정)
     * @param userRole 사용자 역할 (INVESTOR, 추후 시큐리티 적용 시 Authentication에서 추출 예정)
     * @param request  투자자 문의 목록 요청 데이터 (검색 및 필터 조건 포함)
     * @param pageable 페이지네이션 정보 (페이지 크기, 정렬 등)
     * @return 투자자 문의 목록
     */
    @PostMapping("/investor/{userId}/questions")
    public ResponseEntity<BaseResponse<PageResponseDto<QuestionsResponse>>> getInvestorQuestions(
            @PathVariable Long userId,
            @RequestParam Role userRole,
            @RequestBody @Valid QuestionRequestDto request,
            @PageableDefault(size = 4, sort = "createdAt") Pageable pageable) {

        // 추후 시큐리티 적용 시 Authentication 객체로부터 userId와 userRole을 추출
        PageResponseDto<QuestionsResponse> response = questionService.getInvestorQuestions(userId, userRole, request,
                pageable);
        return BaseResponse.success(response);
    }

    /**
     * 트레이더 문의 목록 조회
     *
     * @param userId   트레이더 ID (추후 시큐리티 적용 시 Authentication에서 추출 예정)
     * @param userRole 사용자 역할 (TRADER, 추후 시큐리티 적용 시 Authentication에서 추출 예정)
     * @param request  트레이더 문의 목록 요청 데이터 (검색 및 필터 조건 포함)
     * @param pageable 페이지네이션 정보 (페이지 크기, 정렬 등)
     * @return 트레이더 문의 목록
     */
    @PostMapping("/trader/{userId}/questions")
    public ResponseEntity<BaseResponse<PageResponseDto<QuestionsResponse>>> getTraderQuestions(
            @PathVariable Long userId,
            @RequestParam Role userRole,
            @RequestBody @Valid QuestionRequestDto request,
            @PageableDefault(size = 4, sort = "createdAt") Pageable pageable) {

        // 추후 시큐리티 적용 시 Authentication 객체로부터 userId와 userRole을 추출
        PageResponseDto<QuestionsResponse> response = questionService.getTraderQuestions(userId, userRole, request,
                pageable);
        return BaseResponse.success(response);
    }

    /**
     * 관리자 문의 목록 조회
     *
     * @param userId   관리자 ID (추후 시큐리티 적용 시 Authentication에서 추출 예정)
     * @param userRole 사용자 역할 (ADMIN, 추후 시큐리티 적용 시 Authentication에서 추출 예정)
     * @param request  관리자 문의 목록 요청 데이터 (검색 및 필터 조건 포함)
     * @param pageable 페이지네이션 정보 (페이지 크기, 정렬 등)
     * @return 관리자 문의 목록
     */
    @PostMapping("/admin/{userId}/questions")
    public ResponseEntity<BaseResponse<PageResponseDto<QuestionsResponse>>> getAdminQuestions(
            @PathVariable Long userId,
            @RequestParam Role userRole,
            @RequestBody @Valid QuestionRequestDto request,
            @PageableDefault(size = 8, sort = "createdAt") Pageable pageable) {

        // 추후 시큐리티 적용 시 Authentication 객체로부터 userId와 userRole을 추출
        PageResponseDto<QuestionsResponse> response = questionService.getAdminQuestions(userId, userRole, request,
                pageable);
        return BaseResponse.success(response);
    }

    /**
     * 문의 상세 조회
     *
     * @param questionId 상세 조회할 문의 ID
     * @param userId     사용자 ID (투자자/트레이더 공통)
     * @param userRole   사용자 역할 (INVESTOR/TRADER)
     * @return 문의 상세 정보
     */
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<BaseResponse<QuestionsDetailResponse>> getQuestionDetail(
            @PathVariable Long questionId,
            @RequestParam Long userId,
            @RequestParam Role userRole) {

        // 사용자 역할에 따라 처리
        QuestionsDetailResponse response = questionService.getQuestionDetail(questionId, userId, userRole);
        return BaseResponse.success(response);
    }

    /**
     * 관리자 문의 상세 조회
     *
     * @param questionId 상세 조회할 문의 ID
     * @param userRole   사용자 역할 (ADMIN, 추후 시큐리티 적용 시 Authentication에서 추출 예정)
     * @return 문의 상세 정보
     */
    @GetMapping("/admin/questions/{questionId}")
    public ResponseEntity<BaseResponse<QuestionsDetailResponse>> getAdminQuestionDetail(
            @PathVariable Long questionId,
            @RequestParam Role userRole) {

        // 추후 시큐리티 적용 시 Authentication 객체로부터 userId와 userRole을 추출
        QuestionsDetailResponse response = questionService.getAdminQuestionDetail(questionId, userRole);
        return BaseResponse.success(response);
    }
}
