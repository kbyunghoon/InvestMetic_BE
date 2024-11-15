package com.investmetic.domain.accountverification.service;

import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import com.investmetic.domain.accountverification.repository.AccountVerficationRepository;
import com.investmetic.domain.strategy.dto.request.AccountImageRequestDto;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.dto.MultiPresignedUrlResponseDto;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountImageService {
    private final S3FileService s3FileService;
    private final StrategyRepository strategyRepository;
    private final AccountVerficationRepository accountVerficationRepository;

    public MultiPresignedUrlResponseDto registerStrategyAccountImages(
            Long strategyId,
            List<AccountImageRequestDto> requestDtoList) {
        // 트레이더 정보 확인
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        // 결과를 담을 리스트 생성
        List<PresignedUrlResponseDto> presignedUrlList = new ArrayList<>();

        // 요청된 각 파일에 대해 Presigned URL 생성 및 추가
        for (AccountImageRequestDto accountImageRequestDto : requestDtoList) {
            String filePath = s3FileService.getS3Path(
                    FilePath.STRATEGY_IMAGE,
                    accountImageRequestDto.getFileName() + "_" + accountImageRequestDto.getDate(),
                    accountImageRequestDto.getFileSize()
            );

            // Presigned URL 생성 (예제 URL 생성)
            String presignedUrl = s3FileService.getPreSignedUrl(filePath);

            // PresignedUrlResponseDto 객체 생성 및 리스트에 추가
            PresignedUrlResponseDto presignedUrlResponseDto = PresignedUrlResponseDto.builder()
                    .presignedUrl(presignedUrl)
                    .build();

            AccountVerification accountVerification = AccountVerification.builder()
                    .strategy(strategy)
                    .title(accountImageRequestDto.getDate())
                    .accountVerificationUrl(filePath)
                    .build();

            accountVerficationRepository.save(accountVerification);

            presignedUrlList.add(presignedUrlResponseDto);
        }

        // MultiPresignedUrlResponseDto 객체 생성 후 반환
        return MultiPresignedUrlResponseDto.builder()
                .presignedUrls(presignedUrlList)
                .build();
    }
}
