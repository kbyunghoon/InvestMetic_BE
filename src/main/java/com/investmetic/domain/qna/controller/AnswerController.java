package com.investmetic.domain.qna.controller;

import com.investmetic.domain.qna.dto.request.AnswerRequestDto;
import com.investmetic.domain.qna.service.AnswerService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @RequestParam Long traderId,
            @RequestBody @Valid AnswerRequestDto answerRequestDto) {

        answerService.createAnswer(questionId, traderId, answerRequestDto);
        return BaseResponse.success(SuccessCode.CREATED);
    }
}
