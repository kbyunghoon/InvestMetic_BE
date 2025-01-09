package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.StockTypeRequestDTO;
import com.investmetic.domain.strategy.dto.response.StockTypeResponseDTO;
import com.investmetic.domain.strategy.service.StockTypeService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequiredArgsConstructor
@RequestMapping("/api/admin/strategies")
@Tag(name = "종목 관리 페이지 API", description = "종목 관리 페이지 관련 API")
public class StockTypeController {
    private final StockTypeService stockTypeService;

    @Operation(summary = "종목 조회(관리자 페이지)",
            description = "<a href='https://www.notion.so/034c9ed1b0cf4b9bb598ff96bb34f57c' target='_blank'>API 명세서</a>")
    @GetMapping("/stock-type")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    public ResponseEntity<BaseResponse<PageResponseDto<StockTypeResponseDTO>>> getAllStockTypes(
            @PageableDefault(size = 10, page = 1) Pageable pageable,
            @RequestParam boolean activateState) {
        PageResponseDto<StockTypeResponseDTO> stockTypeResponseDTO = stockTypeService.getStockTypes(pageable,
                activateState);
        return BaseResponse.success(stockTypeResponseDTO);
    }

    @Operation(summary = "종목 등록(관리자 페이지)",
            description = "<a href='https://www.notion.so/de54e885de3c4ba6bcd81f437cbcfb04' target='_blank'>API 명세서</a>")
    @PostMapping("/stock-type")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> addStockType(
            @RequestBody StockTypeRequestDTO stockType) {
        String preSignedURL = stockTypeService.saveStockType(stockType);

        return BaseResponse.success(SuccessCode.CREATED,
                PresignedUrlResponseDto.builder()
                        .presignedUrl(preSignedURL)
                        .build());
    }

    @Operation(summary = "종목 활성화/비활성화(관리자 페이지)",
            description = "<a href='https://www.notion.so/e127e6c077bd4c4bab29d4212f3cebb9' target='_blank'>API 명세서</a>")
    @PatchMapping("stock-type/{stockTypeId}")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    public ResponseEntity<BaseResponse<Void>> updateStockType(@PathVariable Long stockTypeId) {
        stockTypeService.changeActivateState(stockTypeId);
        return BaseResponse.success(SuccessCode.UPDATED);
    }

    @Operation(summary = "종목 삭제(관리자 페이지)")
    @DeleteMapping("stock-type/{stockTypeId}")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteStockType(@PathVariable Long stockTypeId) {
        stockTypeService.deleteStockType(stockTypeId);
        return BaseResponse.success(SuccessCode.DELETED);
    }
}
