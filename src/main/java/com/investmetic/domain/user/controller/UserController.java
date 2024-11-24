package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.dto.response.TraderProfileDto;
import com.investmetic.domain.user.service.UserService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<BaseResponse<Void>> signup(@RequestBody UserSignUpDto userSignUpDto) {
        userService.signUp(userSignUpDto);

        return BaseResponse.success(SuccessCode.CREATED);

    }

    //닉네임 중복 검사
    @GetMapping("/check/nickname")
    public ResponseEntity<BaseResponse<Void>> checkNicknameDuplicate(@RequestParam String nickname) {

        userService.checkNicknameDuplicate(nickname);

        return BaseResponse.success();
    }

    // 이메일 중복 검사
    @GetMapping("/check/email")
    public ResponseEntity<BaseResponse<Void>> checkEmailDuplicate(@RequestParam String email) {

        userService.checkEmailDuplicate(email);

        return BaseResponse.success();
    }

    // 전화번호 중복 검사
    @GetMapping("/check/phone")
    public ResponseEntity<BaseResponse<Void>> checkPhoneDuplicate(@RequestParam String phone) {

        userService.checkPhoneDuplicate(phone);

        return BaseResponse.success();
    }

    /**
     * 트레이더 목록 조회
     *
     * @param orderBy  정렬할 조건, null 가능
     * @param keyword  닉네임 검색 키워드, null 가능
     * @param pageable 현재 페이지, 사이즈 - 디자인에서는 12개로 확인됨.
     */
    @GetMapping("/traders")
    public ResponseEntity<BaseResponse<PageResponseDto<TraderProfileDto>>> getTraderList(
            @RequestParam String orderBy,
            @RequestParam String keyword,
            @PageableDefault(size = 9) Pageable pageable) {
        return BaseResponse.success(userService.getTraderList(orderBy, keyword, pageable));

    }
}