package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.request.TradeTypeRequestDTO;
import com.investmetic.domain.strategy.dto.response.TradeTypeResponseDTO;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeTypeService {
    private final TradeTypeRepository tradeTypeRepository;
    private final S3FileService s3FileService;

    public String saveTradeType(TradeTypeRequestDTO tradeTypeRequestDTO) {
        // 클라이언트에서 입력한 파일 경로로 생성한 이미지 경로 저장
        TradeType tradeType = tradeTypeRequestDTO.toEntity();
        String tradeIconURL = s3FileService.getS3Path(FilePath.STRATEGY_IMAGE, tradeType.getTradeTypeIconURL(),
                tradeTypeRequestDTO.getSize());
        tradeType.changeTradeIconURL(tradeIconURL);
        tradeTypeRepository.save(tradeType);
        return s3FileService.getPreSignedUrl(tradeIconURL);
    }

    public List<TradeTypeResponseDTO> getTradeTypes(Boolean activateState) {
        List<TradeTypeResponseDTO> tradeTypes = tradeTypeRepository.findByActivateState(activateState)
                .stream()
                .map(TradeTypeResponseDTO::from)
                .toList();

        return tradeTypes;
    }
    @Transactional
    public void changeActivateState(Long tradeTypeId) {
        TradeType tradeType = tradeTypeRepository
                .findById(tradeTypeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRADETYPE_NOT_FOUND));
        tradeType.changeActivateState();
        tradeTypeRepository.save(tradeType);
    }
}
