package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.request.EmailRequestDto;
import com.investmetic.domain.user.service.EmailService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/authenticate")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailService emailService;

    // 이메일 인증 코드 전송
    @GetMapping("/email/{email_addr}/authcode")
    public ResponseEntity<BaseResponse<String>> sendEmailPath(
            @PathVariable String email_addr) throws MessagingException {

        emailService.sendEmail(email_addr);
        return BaseResponse.success(SuccessCode.OK);
    }

    // 인증 코드 검증
    @PostMapping("/email/{email_addr}/authcode")
    public ResponseEntity<BaseResponse<Boolean>> sendEmailAndCode(
            @PathVariable String email_addr,
            @RequestBody EmailRequestDto emailRequestDto) {
        boolean response = emailService.verifyEmailCode(email_addr, emailRequestDto.getCode());
        return BaseResponse.success(response);

    }
}