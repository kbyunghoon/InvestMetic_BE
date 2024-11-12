package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.request.EmailRequestDto;
import com.investmetic.domain.user.service.EmailService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.exception.SuccessCode;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
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
    public BaseResponse<String> sendEmailPath(
            @PathVariable String email_addr) throws MessagingException {

        emailService.sendEmail(email_addr);
        return BaseResponse.success();
    }

    // 인증 코드 검증
    @PostMapping("/email/{email_addr}/authcode")
    public BaseResponse<String> sendEmailAndCode(
            @PathVariable String email_addr,
            @RequestBody EmailRequestDto emailRequestDto) {

        if (emailService.verifyEmailCode(email_addr, emailRequestDto.getCode())) {
            return BaseResponse.success(SuccessCode.CREATED, emailRequestDto.getEmail());
        }

        return BaseResponse.fail(ErrorCode.VERIFICATION_FAILED); //인증코드가 일치하지 않을 경우
    }
}