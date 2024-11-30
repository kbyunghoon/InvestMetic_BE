package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.object.TraderListSort;
import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.dto.response.AvaliableDto;
import com.investmetic.domain.user.dto.response.TraderProfileDto;
import com.investmetic.domain.user.service.UserService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "사용자 기본 API", description = "사용자 관련 기본 기능 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 가입",
            description = "<a href='https://www.notion.so/3b51884e19b2420e8800a18ee92c310c' target='_blank'>API 명세서</a>")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<String>> signup(@Valid @RequestBody UserSignUpDto userSignUpDto) {
        // 회원가입시 오류 BusinessException(ErrorCode.SIGN_UP_FAILED)로
        userService.signUp(userSignUpDto);

        return BaseResponse.success(SuccessCode.CREATED);
    }

    //닉네임 중복 검사
    @Operation(summary = "닉네임 중복 확인",
            description = "<a href='https://www.notion.so/79c0ca49115946ff944e4ad53e2cd581' target='_blank'>API 명세서</a>")
    @GetMapping("/check/nickname")
    public ResponseEntity<BaseResponse<AvaliableDto>> checkNicknameDuplicate(@RequestParam String nickname) {

        AvaliableDto response = userService.checkNicknameDuplicate(nickname);

        return BaseResponse.success(response);
    }

    // 이메일 중복 검사
    @Operation(summary = "이메일 중복 확인",
            description = "<a href='https://www.notion.so/c35335e2d75048e5a84bf50cbcb9098e' target='_blank'>API 명세서</a>")
    @GetMapping("/check/email")
    public ResponseEntity<BaseResponse<AvaliableDto>> checkEmailDuplicate(@RequestParam String email) {

        AvaliableDto response = userService.checkEmailDuplicate(email);

        return BaseResponse.success(response);
    }

    // 전화번호 중복 검사
    @Operation(summary = "전화번호 중복 확인",
            description = "<a href='https://www.notion.so/b6445707925c40f1809ba1b92ffe3d01' target='_blank'>API 명세서</a>")
    @GetMapping("/check/phone")
    public ResponseEntity<BaseResponse<AvaliableDto>> checkPhoneDuplicate(@RequestParam String phone) {

        AvaliableDto response = userService.checkPhoneDuplicate(phone);

        return BaseResponse.success(response);
    }

    /**
     * 트레이더 목록 조회
     *
     * @param sort     정렬할 조건, null 가능
     * @param keyword  닉네임 검색 키워드, null 가능
     * @param pageable 현재 페이지, 사이즈 - 디자인에서는 12개로 확인됨.
     */
    @Operation(summary = "트레이더 목록 조회",
            description = "<a href='https://www.notion.so/3e75537ce988485d8477dc5bb5e14cfd' target='_blank'>API 명세서</a>")
    @GetMapping("/traders")
    public ResponseEntity<BaseResponse<PageResponseDto<TraderProfileDto>>> getTraderList(
            @RequestParam TraderListSort sort,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 9) Pageable pageable) {
        return BaseResponse.success(userService.getTraderList(sort, keyword, pageable));

    }
}