package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.request.EmailRequestDto;
import com.investmetic.domain.user.service.UserService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/authenticate")
@RequiredArgsConstructor
public class EmailAuthController {

    private final UserService userService;

    // 이메일 인증 코드 전송
    @GetMapping
    public ResponseEntity<BaseResponse<Void>> sendEmailPath(
            @RequestParam String email) {

        // 코드 생성 및 발송.
        userService.sendAuthenticationCode(email); //코드 redis 저장 있어야함.

        return BaseResponse.success(SuccessCode.OK);
    }

    // 인증 코드 검증
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> sendEmailAndCode(
            @RequestBody EmailRequestDto requestDto) {

        userService.verifyEmailCode(requestDto.getEmail(), requestDto.getCode());

        return BaseResponse.success(SuccessCode.OK);

    }
}