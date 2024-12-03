package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.request.EmailRequestDto;
import com.investmetic.domain.user.service.UserService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="이메일 인증 API", description = "이메일 인증 관련 API")
@RestController
@RequestMapping("/api/users/authenticate")
@RequiredArgsConstructor
public class EmailAuthController {

    private final UserService userService;

    @Operation(summary = "이메일 인증 코드 전송",
            description = "<a href='https://www.notion.so/83bfa140a20843eca3f921e70c13955c' target='_blank'>API 명세서</a>")
    @GetMapping
    public ResponseEntity<BaseResponse<Void>> sendEmailPath(
            @RequestParam String email) {

        // 코드 생성 및 발송.
        userService.sendAuthenticationCode(email); //코드 redis 저장 있어야함.

        return BaseResponse.success(SuccessCode.OK);
    }


    @Operation(summary = "비밀번호 재설정시 이메일 인증 코드 검증",
            description = "<a href='https://www.notion.so/c32cf558a42b49c6962a2698c1052675' target='_blank'>API 명세서</a>")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> sendEmailAndCode(
            @RequestBody EmailRequestDto requestDto) {

        userService.verifyEmailCode(requestDto.getEmail(), requestDto.getCode());

        return BaseResponse.success(SuccessCode.OK);

    }


    @Operation(summary = "회원가입시 이메일 인증 코드 전송",
            description = "<a href='https://www.notion.so/a6dff91d8c204ec2806a9478ff258b33' target='_blank'>API 명세서</a>")
    @GetMapping("/signup")
    public ResponseEntity<BaseResponse<Void>> sendSignUpCode(
            @RequestParam String email) {

        // 코드 생성 및 발송.
        userService.sendSignUpCode(email); //코드 redis 저장 있어야함.

        return BaseResponse.success(SuccessCode.OK);
    }


    // 회원가입시 인증코드 검증
    @Operation(summary = "회원가입시 이메일 인증 코드 검증",
            description = "<a href='https://www.notion.so/1b6156c899da4dbd97c1638faa392128' target='_blank'>API 명세서</a>")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<Void>> checkSignUpCode(
            @RequestBody EmailRequestDto requestDto) {

        userService.verifySignUpEmailCode(requestDto.getEmail(), requestDto.getCode());

        return BaseResponse.success(SuccessCode.OK);
    }


}