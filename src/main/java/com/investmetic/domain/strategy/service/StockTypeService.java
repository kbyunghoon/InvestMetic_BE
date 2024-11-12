package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.request.StockTypeRequestDTO;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockTypeService {
    private final StockTypeRepository stockTypeRepository;
    private final S3FileService s3FileService;

    @Autowired
    public StockTypeService(StockTypeRepository stockTypeRepository, S3FileService s3FileService) {
        this.stockTypeRepository = stockTypeRepository;
        this.s3FileService = s3FileService;
    }

    public String saveStockType(StockTypeRequestDTO stockTypeRequestDTO) {
        StockType stockType = stockTypeRequestDTO.toEntity();
        String stockTypeIconURL = s3FileService.getS3Path(FilePath.STRATEGY_IMAGE, stockType.getStockTypeIconURL(), stockTypeRequestDTO.getSize());

        stockType.changeStockTypeIconURL(stockTypeIconURL);
        stockTypeRepository.save(stockType);
        return s3FileService.getPreSignedUrl(stockTypeIconURL);
    }
}
