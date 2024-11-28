package com.investmetic.domain.qna.controller;

import com.investmetic.domain.qna.dto.request.AnswerRequestDto;
import com.investmetic.domain.qna.service.AnswerService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    //문의 답변 등록
    @PostMapping("/trader/questions/{questionId}/answers")
    public ResponseEntity<BaseResponse<Void>> addAnswer(
            @PathVariable Long questionId,
            @RequestParam Long userId,
            @RequestBody @Valid AnswerRequestDto answerRequestDto) {

        answerService.createAnswer(questionId, userId, answerRequestDto);
        return BaseResponse.success(SuccessCode.CREATED);
    }

    //문의 답변 삭제 (트레이더)
    @DeleteMapping("/trader/questions/{questionId}/answers/{answerId}")
    public ResponseEntity<BaseResponse<Void>> deleteTraderAnswer(
            @PathVariable Long questionId,
            @PathVariable Long answerId,
            @RequestParam Long userId) {

        answerService.deleteTraderAnswer(answerId, questionId, userId);
        return BaseResponse.success(SuccessCode.DELETED);
    }

    //문의 답변 삭제 (관리자)
    @DeleteMapping("/admin/questions/{questionId}/answers/{answerId}")
    public ResponseEntity<BaseResponse<Void>> deleteAdminAnswer(
            @PathVariable Long questionId,
            @PathVariable Long answerId) {

        answerService.deleteAdminAnswer(answerId, questionId);
        return BaseResponse.success(SuccessCode.DELETED);
    }
}
