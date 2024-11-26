package com.investmetic.domain.qna.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StrategyRepository strategyRepository;

    @InjectMocks
    private QuestionService questionService;

    public QuestionServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("문의 생성 성공")
    void from_Success() {
        // Given
        Long userId = 1L;
        Long strategyId = 1L;
        QuestionRequestDto requestDto = new QuestionRequestDto("테스트 제목", "테스트 내용");

        User mockUser = TestEntityFactory.createTestUser();
        TradeType tradeType = TestEntityFactory.createTestTradeType();
        Strategy mockStrategy = TestEntityFactory.createTestStrategy(mockUser, tradeType);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(mockStrategy));
        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        questionService.createQuestion(userId, strategyId, requestDto);

        // Then
        verify(userRepository).findById(userId);
        verify(strategyRepository).findById(strategyId);
        verify(questionRepository).save(any(Question.class));
    }



}