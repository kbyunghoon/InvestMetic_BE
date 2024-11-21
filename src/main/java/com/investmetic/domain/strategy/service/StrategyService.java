package com.investmetic.domain.strategy.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.dto.FileDownloadResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.S3FileService;
import java.io.IOException;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrategyService {
    private final StrategyRepository strategyRepository;
    private final S3FileService s3FileService;
    private final AmazonS3Client amazonS3Client;

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
}