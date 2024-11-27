package com.investmetic.domain.qna.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.investmetic.domain.qna.dto.request.AnswerRequestDto;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.AnswerRepository;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
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
        when(mockTrader.getUserId()).thenReturn(1L);
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

    @Test
    @DisplayName("답변 생성 실패 - 권한 없음")
    void createAnswer_Failure_NoPermission() {
        // Given
        Long questionId = 1L;
        Long userId = 2L; // 권한 없는 사용자 ID

        User mockTrader = mock(User.class);
        when(mockTrader.getUserId()).thenReturn(3L);
        when(mockTrader.getNickname()).thenReturn("트레이더");

        Strategy mockStrategy = mock(Strategy.class);
        when(mockStrategy.getUser()).thenReturn(mock(User.class));

        Question mockQuestion = mock(Question.class);
        when(mockQuestion.getStrategy()).thenReturn(mockStrategy);

        AnswerRequestDto answerRequestDto = new AnswerRequestDto("답변 내용");

        // Mock 설정
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            answerService.createAnswer(questionId, userId, answerRequestDto);
        });

        assertEquals(ErrorCode.FORBIDDEN_ACCESS, exception.getErrorCode()); // 에러 코드 확인
        verify(answerRepository, never()).save(any(Answer.class)); // 답변이 저장되지 않았는지 확인
    }


    @Test
    @DisplayName("답변 삭제 성공 - 관리자")
    void deleteAnswer_Success_Admin() {
        // Given
        Long answerId = 1L;
        Long questionId = 1L;
        Long adminId = 1L;

        User mockAdmin = mock(User.class);
        when(mockAdmin.getUserId()).thenReturn(1L);
        when(mockAdmin.getNickname()).thenReturn("관리자");

        Question mockQuestion = mock(Question.class);
        Answer mockAnswer = mock(Answer.class);

        // Mock 설정
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));
        when(answerRepository.findById(answerId)).thenReturn(Optional.of(mockAnswer));

        // When
        answerService.deleteAnswer(answerId, questionId, Role.SUPER_ADMIN, adminId);

        // Then
        verify(answerRepository).delete(mockAnswer); // 답변 삭제 확인
        verify(mockQuestion).updateQnaState(QnaState.WAITING); // 문의 상태 업데이트 확인
        verify(questionRepository).save(mockQuestion); // 문의 저장 확인
    }

    @Test
    @DisplayName("답변 삭제 성공 - 트레이더 권한")
    void deleteAnswer_Success_Trader() {
        // Given
        Long answerId = 1L;
        Long questionId = 1L;
        Long traderId = 1L;

        User mockTrader = mock(User.class);
        when(mockTrader.getUserId()).thenReturn(1L);
        when(mockTrader.getNickname()).thenReturn("트레이더");

        Strategy mockStrategy = Strategy.builder()
                .user(mockTrader)
                .strategyId(1L)
                .strategyName("테스트 전략")
                .build();

        Question mockQuestion = mock(Question.class);
        when(mockQuestion.getStrategy()).thenReturn(mockStrategy);

        Answer mockAnswer = mock(Answer.class);

        // Mock 설정
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));
        when(answerRepository.findById(answerId)).thenReturn(Optional.of(mockAnswer));

        // When
        answerService.deleteAnswer(answerId, questionId, Role.TRADER, traderId);

        // Then
        verify(answerRepository).delete(mockAnswer); // 삭제 검증
        verify(mockQuestion).updateQnaState(QnaState.WAITING); // 상태 변경 검증
    }


    @Test
    @DisplayName("답변 삭제 실패 - 권한 없음")
    void deleteAnswer_Failure_NoPermission() {
        // Given
        Long answerId = 1L;
        Long questionId = 1L;
        Long userId = 2L; // 권한 없는 사용자

        User mockTrader = mock(User.class);
        when(mockTrader.getUserId()).thenReturn(3L);

        Strategy mockStrategy = mock(Strategy.class);
        when(mockStrategy.getUser()).thenReturn(mockTrader); // 트레이더를 전략과 연결

        Question mockQuestion = mock(Question.class);
        when(mockQuestion.getStrategy()).thenReturn(mockStrategy); // 전략 연결

        Answer mockAnswer = mock(Answer.class);
        when(mockAnswer.getQuestion()).thenReturn(mockQuestion); // 문의 연결

        // Mock 설정
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));
        when(answerRepository.findById(answerId)).thenReturn(Optional.of(mockAnswer));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            answerService.deleteAnswer(answerId, questionId, Role.INVESTOR, userId);
        });
// 검증: 예외 코드 확인
        assertEquals(ErrorCode.FORBIDDEN_ACCESS, exception.getErrorCode());
        verify(answerRepository, never()).delete(any(Answer.class));
        verify(questionRepository).findById(questionId);

    }

    @Test
    @DisplayName("답변 삭제 실패 - 답변 존재하지 않음")
    void deleteAnswer_Failure_AnswerNotFound() {
        // Given
        Long answerId = 1L;
        Long questionId = 1L;
        Long userId = 2L;

        // Mock 설정: Answer가 존재하지 않음
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mock(Question.class)));
        when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            answerService.deleteAnswer(answerId, questionId, Role.TRADER, userId);
        });

        assertEquals(ErrorCode.ANSWER_NOT_FOUND, exception.getErrorCode()); // 에러 코드 확인
        verify(answerRepository, never()).delete(any(Answer.class)); // 삭제되지 않았는지 확인
    }



}
