package com.investmetic.domain.user.controller;

import com.investmetic.global.exception.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/investor")
public class InvestorController {

    @GetMapping
    public ResponseEntity<BaseResponse<String>> getInvestorData() {

        return BaseResponse.success("Investor data");
    }
}
