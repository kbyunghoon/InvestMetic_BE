package com.investmetic.domain.qna.controller;

import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.service.QuestionService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    //문의 등록
    @PostMapping("/strategies/{strategyId}")
    public ResponseEntity<BaseResponse<Void>> addQuestion(
            @PathVariable Long strategyId,
            @RequestParam Long userId,
            @RequestBody @Valid QuestionRequestDto questionRequestDto) {

        questionService.createQuestion(strategyId, userId, questionRequestDto);
        return BaseResponse.success(SuccessCode.CREATED);
    }

    //문의 삭제
    @DeleteMapping("/{questionId}/{strategyId}")
    public ResponseEntity<BaseResponse<Void>> deleteQuestion(
            @PathVariable Long strategyId,
            @PathVariable Long questionId) {

        questionService.deleteQuestion(strategyId, questionId);
        return BaseResponse.success();
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
}