package com.investmetic.domain.qna.controller;

import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.response.AdminQuestionListResponseDto;
import com.investmetic.domain.qna.dto.response.InvestorQuestionListResponseDto;
import com.investmetic.domain.qna.dto.response.TraderQuestionListResponseDto;
import com.investmetic.domain.qna.service.QuestionService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    //문의 등록
    @PostMapping("\"/strategies/{strategyId}/questions\"")
    public ResponseEntity<BaseResponse<Void>> addQuestion(
            @PathVariable Long strategyId,
            @RequestParam Long userId,
            @RequestBody @Valid QuestionRequestDto questionRequestDto) {

        questionService.createQuestion(strategyId, userId, questionRequestDto);
        return BaseResponse.success(SuccessCode.CREATED);
    }

    //문의 삭제
    @DeleteMapping("/strategies/{strategyId}/questions/{questionId}")
    public ResponseEntity<BaseResponse<Void>> deleteQuestion(
            @PathVariable Long strategyId,
            @PathVariable Long questionId) {

        questionService.deleteQuestion(strategyId, questionId);
        return BaseResponse.success(SuccessCode.DELETED);
    }

//    //특정 유저에 대한 모든 문의 삭제 (회원탈퇴 or 추방)
//    @DeleteMapping
//    public ResponseEntity<BaseResponse<Void>> deleteAllQuestionByUser(
//            @PathVariable Long userId) {
//        questionService.deleteAllQuestionByUser(userId);
//        return BaseResponse.success();
//
//
//    }
//
//    //특정 전략에 대한 모든 문의 삭제 (전략 삭제)
//    @DeleteMapping("/{strategyId}/questions")
//    public ResponseEntity<BaseResponse<Void>> deleteAllQuestionByStrategy(
//            @PathVariable Long strategyId) {
//        questionService.deleteAllQuestionByUser(strategyId);
//        return BaseResponse.success();


//    }

    //투자자 문의 목록 조회
    @GetMapping("/users/{userId}/questions")
    public ResponseEntity<BaseResponse<PageResponseDto<InvestorQuestionListResponseDto>>> getInvestorQuestions(
            @PathVariable Long userId,
            @PageableDefault(size = 4, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return questionService.getInvestorQuestionList(userId, pageable);
    }

    //트레이더 문의 목록 조회
    @GetMapping("/strategies/{strategyId}/questions")
    public ResponseEntity<BaseResponse<PageResponseDto<TraderQuestionListResponseDto>>> getTraderQuestions(
            @PathVariable Long strategyId,
            @PageableDefault(size = 4, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return questionService.getTraderQuestionsList(strategyId, pageable);
    }

    //관리자 문의 목록 조회
    @GetMapping("/admins/questions")
    public ResponseEntity<BaseResponse<PageResponseDto<AdminQuestionListResponseDto>>> getAdminQuestions(
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return questionService.getAdminQuestionList(pageable);
    }
}