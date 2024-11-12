package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.StockTypeRequestDTO;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.service.StockTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/strategies")
public class StockTypeController {
    @Autowired
    private StockTypeService stockTypeService;

    @PostMapping("/stock-type")
    public String addStockType(StockTypeRequestDTO stockType) {
        return stockTypeService.saveStockType(stockType);
    }
}
