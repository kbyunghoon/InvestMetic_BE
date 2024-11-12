package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.TradeTypeRequestDTO;
import com.investmetic.domain.strategy.service.TradeTypeService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TradeTypeController {
    @Autowired
    private TradeTypeService tradeTypeService;

    @GetMapping
    public String addStockType(TradeTypeRequestDTO tradeTypeRequestDTO, Integer size){
        return tradeTypeService.saveTradeType(tradeTypeRequestDTO,size);
    }
}
