package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.TradeTypeRequestDTO;
import com.investmetic.domain.strategy.service.TradeTypeService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/strategies")
public class TradeTypeController {
    @Autowired
    private TradeTypeService tradeTypeService;

    @PostMapping("/trade-type")
    public String addStockType(TradeTypeRequestDTO tradeTypeRequestDTO){
        return tradeTypeService.saveTradeType(tradeTypeRequestDTO);
    }
}
