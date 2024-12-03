package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.StrategyModifyRequestDto;
import com.investmetic.domain.strategy.dto.request.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.dto.request.TraderDailyAnalysisRequestDto;
import com.investmetic.domain.strategy.dto.response.DailyAnalysisResponse;
import com.investmetic.domain.strategy.dto.response.MyStrategyDetailResponse;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.dto.response.StrategyModifyInfoResponseDto;
import com.investmetic.domain.strategy.dto.response.common.MyStrategySimpleResponse;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.investmetic.domain.strategy.service.StrategyAnalysisService;
import com.investmetic.domain.strategy.service.StrategyDetailService;
import com.investmetic.domain.strategy.service.StrategyListingService;
import com.investmetic.domain.strategy.service.StrategyService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.dto.FileDownloadResponseDto;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import com.investmetic.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/my-strategies")
@RequiredArgsConstructor
@Tag(name = "전략 API", description = "전략 관련 API")
public class StrategyController {
    private final StrategyAnalysisService strategyAnalysisService;
    private final StrategyDetailService strategyDetailService;
    private final StrategyService strategyService;
    private final StrategyListingService strategyListingService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @Operation(summary = "전략 등록", description = "<a href='https://field-sting-eff.notion.site/9dbecd9a350942a6aa38204329a1c186?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> registerStrategy(
            @RequestBody StrategyRegisterRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return BaseResponse.success(SuccessCode.CREATED,
                strategyService.registerStrategy(requestDto, customUserDetails.getUserId()));
    }

    @GetMapping("/register")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @Operation(summary = "전략 등록 페이지 진입 시 요청", description = "<a href='https://field-sting-eff.notion.site/f1e0b17145a74ace9b5cfec0e6e408ed?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<RegisterInfoResponseDto>> loadStrategyRegistrationInfo() {

        return BaseResponse.success(strategyService.loadStrategyRegistrationInfo());
    }

    @GetMapping("/modify/{strategyId}")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @Operation(summary = "전략 수정 페이지 진입 시 해당 전략 정보 조회", description = "<a href='https://field-sting-eff.notion.site/b5f3a515edd6479f8c22a40732b42475?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<StrategyModifyInfoResponseDto>> loadStrategyModifyInfo(
            @PathVariable Long strategyId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        return BaseResponse.success(strategyService.loadStrategyModifyInfo(strategyId, customUserDetails.getUserId()));
    }

    @PostMapping("/modify/{strategyId}")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @Operation(summary = "전략 수정", description = "<a href='https://field-sting-eff.notion.site/cec6a33cd3ba4d598fd31793c6d086cc?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> modifyStrategyInfo(
            @PathVariable Long strategyId,
            @RequestBody StrategyModifyRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        return BaseResponse.success(SuccessCode.UPDATED,
                strategyService.modifyStrategy(strategyId, requestDto, customUserDetails.getUserId()));
    }

    @PostMapping("/{strategyId}/daily-analysis")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @Operation(summary = "트레이더 전략 일간 분석 등록 기능", description = "<a href='https://field-sting-eff.notion.site/f1e0b17145a74ace9b5cfec0e6e408ed?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<Void>> createStrategyDailyAnalysis(
            @PathVariable Long strategyId,
            @RequestBody List<TraderDailyAnalysisRequestDto> dailyAnalysisRequestDtos,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        strategyAnalysisService.createDailyAnalysis(strategyId, dailyAnalysisRequestDtos,
                customUserDetails.getUserId());

        return BaseResponse.success();
    }

    @PatchMapping("/{strategyId}/daily-analysis")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @Operation(summary = "트레이더 전략 일간 분석 수정 기능", description = "<a href='https://field-sting-eff.notion.site/c9db716164ad405f8f4d4c622476e9f6?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<Void>> modifyStrategyDailyAnalysis(
            @PathVariable Long strategyId,
            @RequestBody TraderDailyAnalysisRequestDto dailyAnalysisRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        strategyAnalysisService.modifyDailyAnalysis(strategyId, dailyAnalysisRequestDto, customUserDetails.getUserId());

        return BaseResponse.success(SuccessCode.UPDATED);
    }

    @PatchMapping("/{strategyId}/visibility")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @Operation(summary = "트레이더 전략 공개 여부 수정 기능", description = "<a href='https://field-sting-eff.notion.site/6a8af82e40814e6db1da806409bc50d7?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<Void>> updateStrategyVisibility(
            @PathVariable Long strategyId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        strategyService.updateVisibility(strategyId, customUserDetails.getUserId());

        return BaseResponse.success(SuccessCode.UPDATED);
    }

    @DeleteMapping("/{strategyId}")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @Operation(summary = "트레이더 전략 삭제 기능", description = "<a href='https://field-sting-eff.notion.site/658d5163ce7642ff9164a80fb25a1d18?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<Void>> deleteStrategy(@PathVariable Long strategyId,
                                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        strategyService.deleteStrategy(strategyId, customUserDetails.getUserId());

        return BaseResponse.success();
    }


    @GetMapping("/{strategyId}/download-proposal")
    @Operation(summary = "트레이더 전략 제안서 다운로드 기능", description = "<a href='https://field-sting-eff.notion.site/0b7c02614c9e485180a3f2e010773c11?pvs=4' target='_blank'>API 명세서</a>")
    public ResponseEntity<Resource> downloadProposal(@PathVariable Long strategyId) {
        FileDownloadResponseDto fileDownloadResponse = strategyService.downloadFileFromUrl(strategyId);

        String encodedFileName = URLEncoder.encode(fileDownloadResponse.getDownloadFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFileName + "\"")
                .body(fileDownloadResponse.getResource());
    }

    @DeleteMapping("{strategyId}/daily-analysis")
    @Operation(summary = "전략 (일간 분석) 삭제(전체 삭제 포함)", description = "<a href='https://field-sting-eff.notion.site/ca5091b0aaa54a39b94c6f1cd4a832af?pvs=4' target='_blank'>API 명세서(1개 삭제)</a><br/><a href='https://field-sting-eff.notion.site/5d021bd7410942e185d6e2025079041c?pvs=4' target='_blank'>API 명세서(전체 삭제)</a>")
    public ResponseEntity<BaseResponse<Void>> deleteStrategyAllDailyAnalysis(@PathVariable Long strategyId,
                                                                             @RequestParam(required = false) Optional<Long> analysisId) {
        analysisId.ifPresentOrElse(
                id -> strategyAnalysisService.deleteStrategyDailyAnalysis(strategyId, id),
                () -> strategyAnalysisService.deleteStrategyAllDailyAnalysis(strategyId)
        );

        return BaseResponse.success(SuccessCode.DELETED);
    }

    @PreAuthorize("hasRole('ROLE_TRADER')")
    @Operation(summary = "트레이더 나의 전략목록 조회(마이페이지) ",
            description = "<a href='https://www.notion.so/2ddd1d0be73a47a7a683394d77943b20' target='_blank'>API 명세서</a>")
    @GetMapping
    public ResponseEntity<BaseResponse<PageResponseDto<MyStrategySimpleResponse>>> getMyStrategies(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 4) Pageable pageable) {
        return BaseResponse.success(strategyListingService.getMyStrategies(customUserDetails.getUserId(), pageable));
    }

    @PreAuthorize("hasRole('ROLE_TRADER') or hasRole('ROLE_INVESTOR')")
    @Operation(summary = "구독한 전략목록 조회(마이페이지) ",
            description = "<a href='https://www.notion.so/5a2dd36508804ca8945692d269c47710' target='_blank'>API 명세서</a>")
    @GetMapping("/subscribed")
    public ResponseEntity<BaseResponse<PageResponseDto<StrategySimpleResponse>>> getSubscribedStrategies(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 8) Pageable pageable) {
        return BaseResponse.success(
                strategyListingService.getSubscribedStrategies(customUserDetails.getUserId(), pageable));
    }

    @PreAuthorize("hasRole('ROLE_INVESTOR')")
    @Operation(summary = "나의 전략 일간분석 조회(마이페이지) ",
            description = "<a href='https://www.notion.so/445709f04679440cbd729c6cabf64f0c' target='_blank'>API 명세서</a>")
    @GetMapping("/{strategyId}/daily-analysis")
    public ResponseEntity<BaseResponse<PageResponseDto<DailyAnalysisResponse>>> getMyDailyAnalysis(
            @PathVariable Long strategyId,
            @PageableDefault(size = 5, sort = "dailyDate", direction = Direction.DESC) Pageable pageable) {
        return BaseResponse.success(strategyAnalysisService.getMyDailyAnalysis(strategyId, pageable));
    }

    @PreAuthorize("hasRole('ROLE_INVESTOR')")
    @Operation(summary = "나의 전략 상세정보 조회(마이페이지) ",
            description = "<a href='https://www.notion.so/445709f04679440cbd729c6cabf64f0c' target='_blank'>API 명세서</a>")
    @GetMapping("/{strategyId}")
    public ResponseEntity<BaseResponse<MyStrategyDetailResponse>> getMyStrategiesDetail(
            @PathVariable Long strategyId) {
        return BaseResponse.success(strategyDetailService.getMyStrategyDetail(strategyId));
    }

}
