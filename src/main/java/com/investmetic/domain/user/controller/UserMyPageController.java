package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.request.PasswordDto;
import com.investmetic.domain.user.dto.request.UserModifyDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.service.UserMyPageService;
import com.investmetic.global.exception.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/mypage")
public class UserMyPageController {

    private final UserMyPageService userMyPageService;

    /**
     * 회원 정보 제공 기능
     * */
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<UserProfileDto>> provideUserInfo(@RequestParam String email) {

        //현재는 requestParam으로 나중에는 jwt, SecurityContext로.

        UserProfileDto userProfileDto = userMyPageService.provideUserInfo(email);

        return BaseResponse.success(userProfileDto);
    }


    /**
     * 회원 정보 수정 기능
     *
     * @param userModifyDto 회원이 수정하려고 하는 정보 데이터.
     * */
    @PatchMapping("/profile")
    public ResponseEntity<BaseResponse<String>> updateUserInfo(@Valid @RequestBody UserModifyDto userModifyDto) {

        // TODO : 현재는 userModifyDto.getEmail로 나중에는 jwt, SecurityContext로.
        String email = userModifyDto.getEmail();

        return BaseResponse.success(userMyPageService.changeUserInfo(userModifyDto, email));
    }

    /**
     * 패스워드 인증
     * */
    @PostMapping("/authenticate/password")
    public ResponseEntity<BaseResponse<Void>> passwordCheck(@RequestBody PasswordDto passwordDto) {

        //@AuthenticationPrincipal
        userMyPageService.checkPassword(passwordDto.getEmail(), passwordDto.getPassword());

        return BaseResponse.success();
    }
}
