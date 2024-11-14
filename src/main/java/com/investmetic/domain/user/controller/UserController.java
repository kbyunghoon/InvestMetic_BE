package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.request.UserSignUpDto;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<UserSignUpDto>> signup(
            @RequestBody UserSignUpDto userSignUpDto) {
        UserSignUpDto userSignUpDto1 = userService.signUp(userSignUpDto);
        return BaseResponse.success(SuccessCode.CREATED, userSignUpDto1);

    }

    //닉네임 중복 검사
    @GetMapping("/check/nickname")
    public ResponseEntity<BaseResponse<Boolean>> checkNicknameDuplicate(
            @RequestParam String nickname) {
        boolean response = userService.checkNicknameDuplicate(nickname);
        return BaseResponse.success(response);
    }

    // 이메일 중복 검사
    @GetMapping("/check/email")
    public ResponseEntity<BaseResponse<Boolean>> checkEmailDuplicate(
            @RequestParam String email) {
        boolean response = userService.checkEmailDuplicate(email);
        return BaseResponse.success(response);
    }

    // 전화번호 중복 검사
    @GetMapping("/check/phone")
    public ResponseEntity<BaseResponse<Boolean>> checkPhoneDuplicate(
            @RequestParam String phone) {
        boolean isDuplicate = userService.checkPhoneDuplicate(phone);
        return BaseResponse.success(isDuplicate);
    }
}