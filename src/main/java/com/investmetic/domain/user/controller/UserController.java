package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.service.UserService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("users/mypage/profile")
    public BaseResponse provideUserInfo(@RequestParam String email) {

        //현재는 requestParam으로 나중에는 jwt, SecurityContext로.

        UserProfileDto userProfileDto = userService.provideUserInfo(email);

        // User정보를 DB로부터 제대로 가져왔을 경우
        if (userProfileDto != null) {
            return BaseResponse.success(userProfileDto);
        }

        // 가져오기 못했을 경우
        return BaseResponse.fail(ErrorCode.USER_INFO_NOT_FOUND);
    }

}
