package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.response.TopSubscriberStrategyResponseDto;
import com.investmetic.domain.strategy.service.MainService;
import com.investmetic.global.common.BaseEntity;
import com.investmetic.global.exception.BaseResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/")
public class MainController {
    private final MainService mainService;

    @GetMapping("/api/strategies/top-ranking")
    public ResponseEntity<BaseResponse<List<TopSubscriberStrategyResponseDto>>> getTopSubscribe(){

        return BaseResponse.success(mainService.getTopSubscriberStrategy());
    }
}
