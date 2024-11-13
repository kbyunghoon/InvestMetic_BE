package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.StockTypeRequestDTO;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.service.StockTypeService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/strategies")
public class StockTypeController {
    @Autowired
    private StockTypeService stockTypeService;

    @PostMapping("/stock-type")
    public ResponseEntity<BaseResponse<String>> addStockType(@RequestBody StockTypeRequestDTO stockType) {
        System.out.println(stockType.getStockTypeIconURL());
        String preSignedURL=stockTypeService.saveStockType(stockType);
        return BaseResponse.success(SuccessCode.CREATED,preSignedURL);
    }
}
