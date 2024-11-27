package com.investmetic.domain.qna.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.qna.dto.request.AnswerRequestDto;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.AnswerRepository;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private AnswerService answerService;

    public AnswerServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("답변 생성 성공")
    void createAnswer_Success() {
        // Given
        Long questionId = 1L;
        Long traderId = 1L;

        // 트레이더 생성
        User mockTrader = mock(User.class);
        when(mockTrader.getUserId()).thenReturn(traderId);
        when(mockTrader.getNickname()).thenReturn("트레이더");

        // 전략 생성
        Strategy mockStrategy = Strategy.builder()
                .strategyId(1L)
                .strategyName("테스트 전략")
                .user(mockTrader)
                .build();

        // 문의 생성
        Question mockQuestion = mock(Question.class);
        when(mockQuestion.getStrategy()).thenReturn(mockStrategy);

        // AnswerRequestDto 생성
        AnswerRequestDto answerRequestDto = new AnswerRequestDto("답변 내용");

        // 답변 생성 (Mock 설정)
        Answer mockAnswer = Answer.from(mockQuestion, answerRequestDto.getContent());
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));
        when(answerRepository.save(any(Answer.class))).thenReturn(mockAnswer);

        // When
        answerService.createAnswer(questionId, traderId, answerRequestDto);

        // Then
        verify(questionRepository).findById(questionId); // 문의 조회 확인
        verify(answerRepository).save(any(Answer.class)); // 답변 저장 확인
        verify(mockQuestion).updateQnaState(QnaState.COMPLETED); // 문의 상태 변경 확인
    }
}
