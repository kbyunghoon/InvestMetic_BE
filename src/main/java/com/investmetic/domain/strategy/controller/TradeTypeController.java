package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.TradeTypeRequestDTO;
import com.investmetic.domain.strategy.dto.response.TradeTypeResponseDTO;
import com.investmetic.domain.strategy.service.TradeTypeService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/strategies")
public class TradeTypeController {

    private final TradeTypeService tradeTypeService;


    @GetMapping("/trade-type")
    public ResponseEntity<BaseResponse<List<TradeTypeResponseDTO>>> getAllTradeTypes(
            @PageableDefault(size = 10, page = 1) Pageable pageable,
            @RequestParam boolean activateState) {
        List<TradeTypeResponseDTO> tradeTypeResponseDTO = tradeTypeService.getTradeTypes(
                activateState);
        return BaseResponse.success(tradeTypeResponseDTO);
    }

    @PostMapping("/trade-type")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> addTradeType(
            @RequestBody TradeTypeRequestDTO tradeTypeRequestDTO) {
        String preSignedURL = tradeTypeService.saveTradeType(tradeTypeRequestDTO);

        return BaseResponse.success(SuccessCode.CREATED,
                PresignedUrlResponseDto.builder()
                        .presignedUrl(preSignedURL)
                        .build());
    }

}
