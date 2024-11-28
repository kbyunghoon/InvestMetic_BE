package com.investmetic.domain.user.controller;


import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import com.investmetic.global.security.service.ReIssueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/api/users/reissue")
public class ReIssueController {

    private final ReIssueService reIssueService;

    @PostMapping("/refreshtoken")
    public ResponseEntity<BaseResponse<Void>> reissue(HttpServletRequest request, HttpServletResponse response) {
        reIssueService.reissueToken(request, response);
        return BaseResponse.success(SuccessCode.OK);
    }
}
