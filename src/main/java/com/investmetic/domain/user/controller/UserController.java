package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.service.UserService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public BaseResponse<String> signup(@RequestBody UserSignUpDto userSignUpDto) {
        BaseResponse<UserProfileDto> response = userService.signUp(userSignUpDto);
        if (response.getIsSuccess()) {
            return BaseResponse.success(SuccessCode.CREATED, userSignUpDto.getEmail());
        }

        // 실패했을 때 (예: 이메일, 닉네임 중복 등)
        return BaseResponse.fail(ErrorCode.SIGN_UP_FAILED);
    }

}
