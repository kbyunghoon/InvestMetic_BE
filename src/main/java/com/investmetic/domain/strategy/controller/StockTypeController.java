package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.StockTypeRequestDTO;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.service.StockTypeService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/strategies")
public class StockTypeController {
    private final StockTypeService stockTypeService;

    @PostMapping("/stock-type")
    public ResponseEntity<BaseResponse<String>> addStockType(@RequestBody StockTypeRequestDTO stockType) {

        String preSignedURL=stockTypeService.saveStockType(stockType);
        return BaseResponse.success(SuccessCode.CREATED,preSignedURL);
    }
}
