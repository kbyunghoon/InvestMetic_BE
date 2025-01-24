package com.investmetic.domain.strategy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.dto.FileDownloadResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.S3FileService;
import java.net.URISyntaxException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileDownloadServiceTest {

    @InjectMocks
    private StrategyService strategyService;

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private S3FileService s3FileService;

    @Test
    @DisplayName("파일 다운로드 - 성공")
    void 테스트_1() throws URISyntaxException {
        Long strategyId = 1L;
        User user = TestEntityFactory.createTestUser();
        String proposalFilePath = "https://s3.amazonaws.com/bucket-name/path/to/file/example.pdf";
        Strategy mockStrategy = Strategy.builder()
                .strategyId(strategyId)
                .user(user)
                .proposalFilePath(proposalFilePath)
                .build();

        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream mockInputStream = mock(S3ObjectInputStream.class);

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(mockStrategy));
        when(s3FileService.extractFileFromUrl(proposalFilePath)).thenReturn(mockS3Object);
        when(mockS3Object.getObjectContent()).thenReturn(mockInputStream);

        FileDownloadResponseDto response = strategyService.downloadFileFromUrl(strategyId, user.getUserId());

        assertNotNull(response);
        assertTrue(response.getDownloadFileName().endsWith(".pdf"));
        assertNotNull(response.getResource());

        verify(strategyRepository, times(1)).findById(strategyId);
        verify(s3FileService, times(1)).extractFileFromUrl(proposalFilePath);
        verify(mockS3Object, times(1)).getObjectContent();
    }

    @Test
    @DisplayName("파일 다운로드 - 실패 (전략 없음)")
    void 테스트_2() {
        Long strategyId = 999L;
        when(strategyRepository.findById(strategyId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                strategyService.downloadFileFromUrl(strategyId, 1L)
        );

        assertEquals(ErrorCode.STRATEGY_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository, times(1)).findById(strategyId);
        verifyNoInteractions(s3FileService);
    }

    @Test
    @DisplayName("파일 다운로드 - 실패 (파일 경로 없음)")
    void 테스트_3() {
        Long strategyId = 1L;
        User user = TestEntityFactory.createTestUser();
        Strategy mockStrategy = Strategy.builder()
                .strategyId(strategyId)
                .user(user)
                .proposalFilePath(null)
                .build();

        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(mockStrategy));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                strategyService.downloadFileFromUrl(strategyId, user.getUserId())
        );

        assertEquals(ErrorCode.PROPOSAL_NOT_FOUND, exception.getErrorCode());
        verify(strategyRepository, times(1)).findById(strategyId);
        verifyNoInteractions(s3FileService);
    }
}
