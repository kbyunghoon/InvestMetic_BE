package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.StockTypeRequestDTO;
import com.investmetic.domain.strategy.dto.response.StockTypeResponseDTO;
import com.investmetic.domain.strategy.service.StockTypeService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/strategies")
public class StockTypeController {
    private final StockTypeService stockTypeService;

    @GetMapping("/stock-type")
    public ResponseEntity<BaseResponse<PageResponseDto<StockTypeResponseDTO>>> getAllStockTypes(
            @PageableDefault(size = 10, page = 1) Pageable pageable,
            @RequestParam boolean activateState) {
        PageResponseDto<StockTypeResponseDTO> stockTypeResponseDTO = stockTypeService.getStockTypes(pageable,
                activateState);
        return BaseResponse.success(stockTypeResponseDTO);
    }

    @PostMapping("/stock-type")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> addStockType(
            @RequestBody StockTypeRequestDTO stockType) {
        String preSignedURL = stockTypeService.saveStockType(stockType);

        return BaseResponse.success(SuccessCode.CREATED,
                PresignedUrlResponseDto.builder()
                        .presignedUrl(preSignedURL)
                        .build());
    }

    @PatchMapping("stock-type/{stockTypeId}")
    public ResponseEntity<BaseResponse<Void>> updateStockType(@PathVariable Long stockTypeId) {
        stockTypeService.changeActivateState(stockTypeId);
        return BaseResponse.success(SuccessCode.UPDATED);
    }

}
