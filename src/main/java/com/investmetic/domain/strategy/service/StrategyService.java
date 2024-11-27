package com.investmetic.domain.strategy.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.dto.request.StrategyModifyRequestDto;
import com.investmetic.domain.strategy.dto.request.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.dto.response.StrategyModifyInfoResponseDto;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.StockTypeGroup;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StockTypeGroupRepository;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.dto.FileDownloadResponseDto;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrategyService {
    private final StrategyRepository strategyRepository;
    private final S3FileService s3FileService;
    private final TradeTypeRepository tradeTypeRepository;
    private final StockTypeRepository stockTypeRepository;
    private final UserRepository userRepository;
    private final StockTypeGroupRepository stockTypeGroupRepository;


    @Transactional
    public void updateVisibility(Long strategyId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        // FIXME : 권한 체크 로직 추가 예정
//        if (strategy.getCreatedBy() != user) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
//        }

        strategy.setIsPublic(strategy.getIsPublic() == IsPublic.PUBLIC ? IsPublic.PRIVATE : IsPublic.PUBLIC);
    }

    @Transactional(readOnly = true)
    public FileDownloadResponseDto downloadFileFromUrl(Long strategyId) {
        // 전략 조회 및 유효성 검사
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        String proposalFilePath = strategy.getProposalFilePath();
        if (proposalFilePath == null || proposalFilePath.isBlank()) {
            throw new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND);
        }

        // S3 파일 키 추출 및 파일 가져오기
        try (
                S3Object s3Object = s3FileService.extractFileKeyFromUrl(proposalFilePath);
                S3ObjectInputStream inputStream = s3Object.getObjectContent()
        ) {
            String fileExtension = extractFileExtension(proposalFilePath);

            // 다운로드 시 파일명 변경
            String newFileName = strategy.getStrategyName() + "_제안서" + fileExtension;

            // InputStreamResource 생성
            InputStreamResource resource = new InputStreamResource(inputStream);

            return FileDownloadResponseDto.builder()
                    .downloadFileName(newFileName)
                    .resource(resource)
                    .build();

        } catch (URISyntaxException | IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 파일 확장자 추출 메서드
    private String extractFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
            return ""; // 확장자가 없거나 잘못된 경우 빈 문자열 반환
        }
        return filePath.substring(lastDotIndex); // "." 포함하여 반환
    }

    @Transactional
    public void deleteStrategy(Long strategyId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        // FIXME : 권한 체크 로직 추가 예정
//        if (strategy.getCreatedBy() != user) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
//        }

        strategyRepository.deleteById(strategyId);
    }


    @Transactional
    public PresignedUrlResponseDto registerStrategy(
            StrategyRegisterRequestDto requestDto) {
        // TODO: 추후 삭제 ----------
        // TODO: 유저 가져오기, tradeType 가져오기, stockType 추가 예정
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));
        // 1. TradeType 조회 (예제용 코드로 실제 구현 시 TradeTypeService를 사용하여 조회)
        TradeType tradeType = tradeTypeRepository.findByTradeTypeId(requestDto.getTradeTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRADETYPE_NOT_FOUND));
        // TODO: 추후 삭제 ----------

        // 2. 제안서 파일 경로 생성 및 Presigned URL 생성
        String proposalFilePath = s3FileService.getS3Path(
                FilePath.STRATEGY_PROPOSAL,
                requestDto.getProposalFile().getProposalFileName(),
                requestDto.getProposalFile().getProposalFileSize()
        );

        String presignedUrl = s3FileService.getPreSignedUrl(proposalFilePath);

        // 3. Strategy 생성 및 저장
        Strategy strategy = Strategy.builder()
                .user(user)
                .strategyName(requestDto.getStrategyName())
                .tradeType(tradeType)
                .operationCycle(requestDto.getOperationCycle())
                .minimumInvestmentAmount(requestDto.getMinimumInvestmentAmount())
                .proposalFilePath(proposalFilePath)
                .strategyDescription(requestDto.getDescription())
                .build();

        strategyRepository.save(strategy);

        requestDto.getStockTypeIds().forEach(stockTypeId -> {
            StockType stockType = stockTypeRepository.findById(stockTypeId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.STOCKTYPE_NOT_FOUND));

            StockTypeGroup stockTypeGroup = StockTypeGroup.builder()
                    .strategy(strategy)
                    .stockType(stockType)
                    .build();

            stockTypeGroupRepository.save(stockTypeGroup);
        });

        return PresignedUrlResponseDto.builder().presignedUrl(presignedUrl).build();
    }


    @Transactional
    public PresignedUrlResponseDto modifyStrategy(
            Long strategyId,
            StrategyModifyRequestDto requestDto) {
        // TODO: 추후 삭제 ----------
        // TODO: 유저 가져오기, tradeType 가져오기, stockType 추가 예정
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        // 1. TradeType 조회 (예제용 코드로 실제 구현 시 TradeTypeService를 사용하여 조회)
        // TODO: 추후 삭제 ----------

        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        strategy.modifyStrategy(requestDto.getStrategyName(), requestDto.getDescription());

        if (Boolean.TRUE.equals(requestDto.getProposalModified())) {
            // 2. 제안서 파일 경로 생성 및 Presigned URL 생성
            String proposalFilePath = s3FileService.getS3Path(
                    FilePath.STRATEGY_PROPOSAL,
                    requestDto.getProposalFile().getProposalFileName(),
                    requestDto.getProposalFile().getProposalFileSize()
            );

            String presignedUrl = s3FileService.getPreSignedUrl(proposalFilePath);
            return PresignedUrlResponseDto.builder().presignedUrl(presignedUrl).build();
        } else {
            return null;
        }
    }

    public RegisterInfoResponseDto loadStrategyRegistrationInfo() {
        List<TradeTypeDto> tradeTypesDto = getActiveTradeTypes();
        List<StockTypeDto> stockTypesDto = getAllStockTypes();

        return buildRegisterInfoResponse(tradeTypesDto, stockTypesDto);
    }

    public StrategyModifyInfoResponseDto loadStrategyModifyInfo(Long strategyId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        TradeTypeDto tradeTypeDto = TradeTypeDto.fromEntity(strategy.getTradeType());

        return StrategyModifyInfoResponseDto.builder()
                .strategy(strategy)
                .stockTypes(getStrategyStockTypes(strategy))
                .tradeType(tradeTypeDto)
                .build();
    }

    private List<TradeTypeDto> getActiveTradeTypes() {
        return tradeTypeRepository.findByActivateStateTrue().stream()
                .map(TradeTypeDto::fromEntity)
                .toList();
    }

    private List<StockTypeDto> getStrategyStockTypes(Strategy strategy) {
        List<StockType> stockTypes = stockTypeGroupRepository.findStockTypeIdsByStrategy(strategy);

        return stockTypes.stream()
                .map(StockTypeDto::from)
                .toList();
    }

    private List<StockTypeDto> getAllStockTypes() {
        return stockTypeRepository.findAll().stream()
                .map(StockTypeDto::from)
                .toList();
    }

    private RegisterInfoResponseDto buildRegisterInfoResponse(
            List<TradeTypeDto> tradeTypes,
            List<StockTypeDto> stockTypes
    ) {
        return RegisterInfoResponseDto.builder()
                .tradeTypes(tradeTypes)
                .stockTypes(stockTypes)
                .build();
    }
}