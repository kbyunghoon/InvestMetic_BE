package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.request.TradeTypeRequestDTO;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeTypeService {
    private final TradeTypeRepository tradeTypeRepository;
    private final S3FileService s3FileService;

    @Autowired
    public TradeTypeService(TradeTypeRepository tradeTypeRepository, S3FileService s3FileService) {
        this.tradeTypeRepository = tradeTypeRepository;
        this.s3FileService = s3FileService;
    }

    public String saveTradeType(TradeTypeRequestDTO tradeTypeRequestDTO) {
        // 클라이언트에서 입력한 파일 경로로 생성한 이미지 경로 저장
        TradeType tradeType=tradeTypeRequestDTO.toEntity();
        String tradeIconURL = s3FileService.getS3Path(FilePath.STRATEGY_IMAGE, tradeType.getTradeIconURL(), tradeTypeRequestDTO.getSize());
        tradeType.changeTradeIconURL(tradeIconURL);
        tradeTypeRepository.save(tradeType);
        return s3FileService.getPreSignedUrl(tradeIconURL);
    }

}
