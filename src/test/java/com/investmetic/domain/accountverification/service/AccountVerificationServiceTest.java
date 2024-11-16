package com.investmetic.domain.accountverification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.accountverification.dto.response.AccountImagesResponseDto;
import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import com.investmetic.domain.accountverification.repository.AccountVerificationRepository;
import com.investmetic.domain.strategy.dto.request.AccountImageRequestDto;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.dto.MultiPresignedUrlResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class AccountVerificationServiceTest {

    @Mock
    private S3FileService s3FileService;

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TradeTypeRepository tradeTypeRepository;

    @Mock
    private AccountVerificationRepository accountVerificationRepository;

    @InjectMocks
    private AccountVerificationService accountVerificationService;

    private Strategy strategy;
    private AccountVerification accountVerification1;
    private AccountVerification accountVerification2;
    private List<AccountImageRequestDto> requestDtoList;

    /**
     * 테스트용 Strategy와 AccountVerification 엔티티를 생성 및 요청 DTO 초기화
     */
    @BeforeEach
    void setUp() {
        strategy = Strategy.builder()
                .strategyId(1L)
                .strategyName("Test Strategy")
                .build();

        accountVerification1 = AccountVerification.builder()
                .strategy(strategy)
                .title("Verification 1")
                .accountVerificationUrl("http://example.com/image1")
                .build();

        accountVerification2 = AccountVerification.builder()
                .strategy(strategy)
                .title("Verification 2")
                .accountVerificationUrl("http://example.com/image2")
                .build();

        AccountImageRequestDto requestDto = AccountImageRequestDto.builder()
                .fileName("test-image")
                .title("테스트")
                .fileSize(1024)
                .build();

        requestDtoList = new ArrayList<>();
        requestDtoList.add(requestDto);
    }

    /**
     * 전략이 데이터베이스에 존재할 경우, 계좌 인증 이미지 등록 성공 테스트
     */
    @Test
    void 실계좌_인증_이미지_등록_성공_테스트() {
        when(strategyRepository.findById(1L)).thenReturn(Optional.of(strategy));
        when(s3FileService.getS3Path(eq(FilePath.STRATEGY_IMAGE), anyString(), anyInt()))
                .thenReturn("s3://bucket/path/to/image");
        when(s3FileService.getPreSignedUrl("s3://bucket/path/to/image"))
                .thenReturn("https://presigned.url");

        MultiPresignedUrlResponseDto response = accountVerificationService.registerStrategyAccountImages(1L,
                requestDtoList);

        assertThat(response.getPresignedUrls()).hasSize(1);
        assertThat(response.getPresignedUrls().get(0).getPresignedUrl()).isEqualTo("https://presigned.url");

        verify(strategyRepository, times(1)).findById(1L);
        verify(accountVerificationRepository, times(1)).save(any(AccountVerification.class));
    }

    /**
     * 계좌 인증 이미지 등록 시 전략이 존재하지 않을 경우 예외 발생 테스트. 전략이 데이터베이스에 존재하지 않을 경우, BusinessException 예외 발생해야 함.
     */
    @Test
    void 실계좌_인증_이미지_등록_전략_없을_경우() {
        when(strategyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountVerificationService.registerStrategyAccountImages(1L, requestDtoList))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.STRATEGY_NOT_FOUND.getMessage());

        verify(accountVerificationRepository, never()).save(any(AccountVerification.class));
    }


    @Test
    void 실계좌_인증_조회_성공_테스트() {
        // Given
        Long strategyId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        // Mock 설정
        Page<AccountVerification> mockPage = new PageImpl<>(List.of(accountVerification1, accountVerification2),
                pageable, 2);
        when(accountVerificationRepository.findByStrategy_StrategyId(strategyId, pageable)).thenReturn(mockPage);

        // When
        PageResponseDto<AccountImagesResponseDto> response = accountVerificationService.getAccountImagesByStrategyId(
                strategyId, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getPage()).isEqualTo(1); // 1부터 시작하도록 설정한 것 확인
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("Verification 1");
        assertThat(response.getContent().get(1).getTitle()).isEqualTo("Verification 2");
    }

    @Test
    void 실계좌_정보_조회_결과_없음_테스트() {
        // Given
        Long strategyId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        // Mock 설정: 빈 페이지 반환
        Page<AccountVerification> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(accountVerificationRepository.findByStrategy_StrategyId(strategyId, pageable)).thenReturn(emptyPage);

        // When
        PageResponseDto<AccountImagesResponseDto> response = accountVerificationService.getAccountImagesByStrategyId(
                strategyId, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getPage()).isEqualTo(1); // 1부터 시작하도록 설정한 것 확인
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getTotalPages()).isZero();
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
    }
}