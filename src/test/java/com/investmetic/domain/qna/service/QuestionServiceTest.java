package com.investmetic.domain.qna.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.qna.dto.SearchCondition;
import com.investmetic.domain.qna.dto.StateCondition;
import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.response.QuestionsPageResponse;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Test
    @DisplayName("문의 삭제 성공")
    void deleteQuestion_Success() {
        // Given
        Long strategyId = 1L;
        Long questionId = 1L;
        Long userId = 1L;

        // 사용자(Mock User) 생성
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn(userId);
        when(mockUser.getRole()).thenReturn(Role.INVESTOR);
        when(mockUser.getNickname()).thenReturn("TestUser");


        // 전략(Mock Strategy) 생성 (사용자 설정 필수)
        Strategy mockStrategy = Strategy.builder()
                .strategyId(strategyId)
                .user(mockUser) // 전략의 사용자 설정
                .build();

        // 문의(Mock Question) 생성
        QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
                .title("문의 제목")
                .content("내용 ㅋㅋ")
                .build();
        Question mockQuestion = Question.from(mockUser, mockStrategy,questionRequestDto);

        // Mock 설정
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(mockStrategy));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));

        // When
        questionService.deleteQuestion(strategyId, questionId, userId);

        // Then
        verify(userRepository).findById(userId); // 사용자 조회 검증
        verify(strategyRepository).findById(strategyId); // 전략 조회 검증
        verify(questionRepository).findById(questionId); // 문의 조회 검증
        verify(questionRepository).delete(mockQuestion); // 삭제 호출 검증
    }

    @Test
    @DisplayName("투자자 문의 목록 조회 성공 - 실제 객체 사용")
    void getInvestorQuestions_Success_UsingRealObjects() {
        // Given
        Long userId = 1L;
        String keyword = "문의 제목";
        SearchCondition searchCondition = SearchCondition.TITLE;
        StateCondition stateCondition = StateCondition.ALL;
        String strategyName = "전략명";
        String traderName = "트레이더명";
        Pageable pageable = PageRequest.of(0, 10);
        Role role = Role.INVESTOR;

        // 사용자 실제 객체 생성
        User mockUser = User.builder()
                .nickname("TestUser")
                .role(Role.INVESTOR)
                .imageUrl("userImageUrl")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // 전략 실제 객체 생성
        Strategy mockStrategy = Strategy.builder()
                .strategyId(1L)
                .strategyName(strategyName)
                .user(mockUser)
                .build();

        // 문의 실제 객체 생성
        QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
                .title("문의 제목")
                .content("문의 내용")
                .build();

        Question mockQuestion = Question.from(mockUser, mockStrategy,questionRequestDto);
        mockQuestion.updateQnaState(QnaState.WAITING); // qnaState 설정

        Page<Question> questionPage = new PageImpl<>(Collections.singletonList(mockQuestion));

        when(questionRepository.searchQuestions(
                eq(userId),
                eq(keyword),
                eq(searchCondition),
                eq(stateCondition),
                eq(role),
                eq(pageable),
                eq(strategyName),
                eq(traderName),
                isNull()
        )).thenReturn(questionPage);

        // When
        QuestionsPageResponse response = questionService.getInvestorQuestions(
                userId, keyword, searchCondition, stateCondition, strategyName, traderName, pageable, role
        );

        // Then
        assertNotNull(response);
        assertEquals(1, response.getPage().getContent().size());
        assertEquals("문의 제목", response.getPage().getContent().get(0).getTitle());
        verify(userRepository).findById(userId);
        verify(questionRepository).searchQuestions(
                userId, keyword, searchCondition, stateCondition, role, pageable, strategyName, traderName, null
        );
    }


}