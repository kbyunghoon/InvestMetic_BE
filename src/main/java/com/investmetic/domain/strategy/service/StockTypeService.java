package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.request.StockTypeRequestDTO;
import com.investmetic.domain.strategy.dto.response.StockTypeResponseDTO;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.repository.StockTypeGroupRepository;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockTypeService {
    private final StockTypeRepository stockTypeRepository;
    private final S3FileService s3FileService;
    private final StockTypeGroupRepository stockTypeGroupRepository;

    public String saveStockType(StockTypeRequestDTO stockTypeRequestDTO) {
        StockType stockType = stockTypeRequestDTO.toEntity();
        String stockTypeIconUrl = s3FileService.getS3Path(FilePath.STRATEGY_IMAGE, stockType.getStockTypeIconUrl(),
                stockTypeRequestDTO.getSize());
        stockType.changeStockTypeIconURL(stockTypeIconUrl);
        stockTypeRepository.save(stockType);
        return s3FileService.getPreSignedUrl(stockTypeIconUrl);
    }

    public PageResponseDto<StockTypeResponseDTO> getStockTypes(Pageable pageable, Boolean activateState) {
        Page<StockTypeResponseDTO> stocks = stockTypeRepository.findByActivateState(activateState, pageable)
                .map(StockTypeResponseDTO::from);

        return new PageResponseDto<>(stocks);
    }
    @Transactional
    public void changeActivateState(Long StockTypeId) {
        StockType stockType = stockTypeRepository
                .findByStockTypeId(StockTypeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCKTYPE_NOT_FOUND));
        stockType.changeActivateState();
        stockTypeRepository.save(stockType);
    }

    @Transactional
    public void deleteStockType(Long stockTypeId) {
        StockType stockType = stockTypeRepository.findByStockTypeId(stockTypeId).orElseThrow(() -> new BusinessException(ErrorCode.STOCKTYPE_NOT_FOUND));
        stockTypeGroupRepository.deleteAllByStockType(stockType);
        stockTypeRepository.deleteById(stockTypeId);
    }

}
