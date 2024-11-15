package com.investmetic.domain.accountverification.controller;

import com.investmetic.domain.accountverification.service.AccountImageService;
import com.investmetic.domain.strategy.dto.request.AccountImageRequestDto;
import com.investmetic.global.dto.MultiPresignedUrlResponseDto;
import com.investmetic.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/strategies")
@RequiredArgsConstructor
@Tag(name = "마이페이지 트레이더 API", description = "마이페이지 트레이더 전략 관련 API")
public class AccountVerficationController {
    private final AccountImageService accountImageService;

    @PostMapping("/{strategyId}/account-images")
    @Operation(summary = "트레이더 전략 실계좌 인증 이미지 등록 기능", description = "<a href='https://field-sting-eff.notion.site/fe8187f51c9c4a199622b932a1985458?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<MultiPresignedUrlResponseDto>> registerStrategyAccountImages(
            @PathVariable Long strategyId,
            @RequestBody List<AccountImageRequestDto> requestDto
    ) {
        return BaseResponse.success(accountImageService.registerStrategyAccountImages(strategyId, requestDto));
    }
}