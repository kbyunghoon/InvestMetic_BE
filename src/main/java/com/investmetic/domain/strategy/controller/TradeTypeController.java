package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.TradeTypeRequestDTO;
import com.investmetic.domain.strategy.dto.response.TradeTypeResponseDTO;
import com.investmetic.domain.strategy.service.TradeTypeService;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/strategies")
@Tag(name = "매매유형 관리 페이지 API", description = "매매유형 관리 페이지 관련 API")
public class TradeTypeController {

    private final TradeTypeService tradeTypeService;

    @Operation(summary = "매매유형 전체 조회(관리자 페이지)",
            description = "<a href='https://www.notion.so/e7dee00b44c344c485cbbc7d2c69e4bb' target='_blank'>API 명세서</a>")
    @GetMapping("/trade-type")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    public ResponseEntity<BaseResponse<List<TradeTypeResponseDTO>>> getAllTradeTypes(
            @PageableDefault(size = 10, page = 1) Pageable pageable,
            @RequestParam boolean activateState) {
        List<TradeTypeResponseDTO> tradeTypeResponseDTO = tradeTypeService.getTradeTypes(
                activateState);
        return BaseResponse.success(tradeTypeResponseDTO);
    }

    @Operation(summary = "매매유형 등록(관리자 페이지)",
            description = "<a href='https://www.notion.so/aca1735bd984421fb276e709c1725efd' target='_blank'>API 명세서</a>")
    @PostMapping("/trade-type")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> addTradeType(
            @RequestBody TradeTypeRequestDTO tradeTypeRequestDTO) {
        String preSignedURL = tradeTypeService.saveTradeType(tradeTypeRequestDTO);

        return BaseResponse.success(SuccessCode.CREATED,
                PresignedUrlResponseDto.builder()
                        .presignedUrl(preSignedURL)
                        .build());
    }

    @Operation(summary = "매매유형 활성화/비활성화(관리자 페이지)",
            description = "<a href='https://www.notion.so/d73831ed7a7147c29528c4dcbccb0348' target='_blank'>API 명세서</a>")
    @PatchMapping("/trade-type/{tradeTypeId}")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    public ResponseEntity<BaseResponse<Void>> updateTradeType(@PathVariable Long tradeTypeId) {
        tradeTypeService.changeActivateState(tradeTypeId);
        return BaseResponse.success(SuccessCode.UPDATED);
    }

}
