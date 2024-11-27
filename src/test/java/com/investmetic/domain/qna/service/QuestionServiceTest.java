package com.investmetic.domain.qna.service;

import static com.investmetic.domain.user.model.Role.TRADER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
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
import com.investmetic.domain.qna.dto.response.QuestionsDetailResponse;
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
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
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
    @DisplayName("Question.updateQnaState - 상태 업데이트 성공")
    void updateQnaState_Success() {
        // Given
        User mockUser = User.builder()
                .nickname("투자자")
                .role(Role.INVESTOR)
                .build();

        User trader = User.builder()
                .nickname("트레이더")
                .role(Role.TRADER)
                .build();

        Strategy mockStrategy = Strategy.builder()
                .strategyId(1L)
                .strategyName("테스트 전략")
                .user(trader) // 전략 작성자(트레이더)
                .build();

        QuestionRequestDto request = QuestionRequestDto.builder()
                .title("테스트 문의")
                .content("테스트 내용")
                .build();

        Question question = Question.from(mockUser, mockStrategy, request);

        // When
        question.updateQnaState(QnaState.COMPLETED); // 상태 업데이트

        // Then
        assertEquals(QnaState.COMPLETED, question.getQnaState()); // 상태가 변경되었는지 확인
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
        Question mockQuestion = Question.from(mockUser, mockStrategy, questionRequestDto);

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
    @DisplayName("문의 삭제 실패 - 관리자 권한 없음")
    void deleteQuestion_NoAdminPermission() {
        // Given
        Long strategyId = 1L;
        Long questionId = 1L;
        Long userId = 1L;

        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn(2L);
        when(mockUser.getRole()).thenReturn(Role.INVESTOR);
        when(mockUser.getNickname()).thenReturn("TestUser");

        // 전략 Mock
        Strategy mockStrategy = mock(Strategy.class);
        // 문의 Mock
        User mockQuestionUser = mock(User.class);
        when(mockQuestionUser.getUserId()).thenReturn(2L); // 문의 작성자 ID
        Question mockQuestion = mock(Question.class);
        when(mockQuestion.getUser()).thenReturn(mockQuestionUser); // 문의 작성자는 다른 사용자로 설정

        // Repository Mock 설정
        when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(mockStrategy));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            questionService.deleteQuestion(strategyId, questionId, userId);
        });

        assertEquals(ErrorCode.FORBIDDEN_ACCESS, exception.getErrorCode());
        verify(strategyRepository).findById(strategyId);
        verify(userRepository).findById(userId);
        verify(questionRepository).findById(questionId);
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

        Question mockQuestion = Question.from(mockUser, mockStrategy, questionRequestDto);
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

    @Test
    @DisplayName("트레이더 문의 목록 조회 성공")
    void getTraderQuestions_Success() {
        // Given
        Long traderId = 1L;
        String keyword = "문의 제목";
        SearchCondition searchCondition = SearchCondition.TITLE;
        StateCondition stateCondition = StateCondition.ALL;
        String investorName = "투자자1";
        String strategyName = "전략1";
        PageRequest pageable = PageRequest.of(0, 10);

        // Mock User (트레이더)
        User mockTrader = User.builder()
                .role(TRADER)
                .nickname("트레이더1")
                .build();

        // Mock Strategy
        Strategy mockStrategy = Strategy.builder()
                .strategyId(1L)
                .strategyName(strategyName)
                .user(mockTrader)
                .build();

        // Mock Question
        QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
                .title("문의 제목")
                .content("문의 내용")
                .build();
        Question mockQuestion = Question.from(mockTrader, mockStrategy, questionRequestDto);
        Page<Question> mockPage = new PageImpl<>(Collections.singletonList(mockQuestion));

        when(userRepository.findById(traderId)).thenReturn(Optional.of(mockTrader));
        when(questionRepository.searchQuestions(
                traderId, keyword, searchCondition, stateCondition, TRADER, pageable, strategyName, null, investorName
        )).thenReturn(mockPage);

        // When
        QuestionsPageResponse response = questionService.getTraderQuestions(
                traderId, keyword, searchCondition, stateCondition, investorName, strategyName, pageable
        );

        // Then
        assertNotNull(response);
        assertEquals(1, response.getPage().getContent().size());
        assertEquals("문의 제목", response.getPage().getContent().get(0).getTitle());
        verify(userRepository).findById(traderId);
        verify(questionRepository).searchQuestions(
                traderId, keyword, searchCondition, stateCondition, TRADER, pageable, strategyName, null, investorName
        );
    }

    @Test
    @DisplayName("관리자 문의 목록 조회 성공 - 검색 조건 포함")
    void getAdminQuestions_Success() {
        // Given
        Long adminId = 1L;
        String keyword = "문의 제목";
        SearchCondition searchCondition = SearchCondition.TITLE; // 제목 검색
        StateCondition stateCondition = StateCondition.WAITING; // WAITING 상태
        String investorName = "투자자1";
        String strategyName = "전략1";
        String traderName = "트레이더1";
        PageRequest pageable = PageRequest.of(0, 10);

        User mockAdmin = User.builder()
                .userName("admin")
                .role(Role.SUPER_ADMIN) // 관리자 역할
                .nickname("관리자")
                .build();

        User mockUser1 = User.builder()
                .userName("investor")
                .role(Role.INVESTOR)
                .nickname(investorName)
                .build();

        User mockUser2 = User.builder()
                .userName("trader")
                .role(Role.TRADER)
                .nickname(traderName)
                .build();

        Strategy mockStrategy = Strategy.builder()
                .strategyId(1L)
                .strategyName(strategyName)
                .user(mockUser2)
                .build();

        QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
                .title(keyword)
                .content("문의 내용")
                .build();
        Question mockQuestion = Question.from(mockUser1, mockStrategy, questionRequestDto);

        Page<Question> mockPage = new PageImpl<>(Collections.singletonList(mockQuestion));

        // Mockito 설정
        when(userRepository.findById(adminId)).thenReturn(Optional.of(mockAdmin)); // 관리자 확인
        when(questionRepository.searchQuestions(
                eq(adminId), eq(keyword), eq(searchCondition), eq(stateCondition), eq(Role.SUPER_ADMIN),
                eq(pageable), eq(strategyName), eq(traderName), eq(investorName)
        )).thenReturn(mockPage);

        // When
        QuestionsPageResponse response = questionService.getAdminQuestions(
                adminId, keyword, searchCondition, stateCondition, investorName, strategyName, traderName, pageable,
                Role.SUPER_ADMIN
        );

        // Then
        assertNotNull(response);
        assertEquals(1, response.getPage().getContent().size());
        assertEquals("문의 제목", response.getPage().getContent().get(0).getTitle());
        assertEquals("투자자1", response.getPage().getContent().get(0).getInvestorName());
        assertEquals("트레이더1", response.getPage().getContent().get(0).getTraderName());
        assertEquals("전략1", response.getPage().getContent().get(0).getStrategyName());
        verify(userRepository).findById(adminId);
        verify(questionRepository).searchQuestions(
                adminId, keyword, searchCondition, stateCondition, Role.SUPER_ADMIN, pageable, strategyName, traderName,
                investorName
        );
    }

    @Test
    @DisplayName("문의 상세 조회 성공 - 관리자")
    void getQuestionDetail_Admin_Success() {
        // Given
        Long questionId = 1L;
        Long adminId = 1L;

        User mockAdmin = User.builder()
                .userName("admin")
                .role(Role.SUPER_ADMIN)
                .nickname("관리자")
                .build();

        User mockTrader = User.builder()
                .userName("trader")
                .nickname("트레이더")
                .role(Role.TRADER)
                .build();

        Strategy mockStrategy = Strategy.builder()
                .strategyId(1L)
                .strategyName("전략명")
                .user(mockTrader)
                .build();

        QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
                .title("문의 제목")
                .content("문의 내용")
                .build();
        Question mockQuestion = Question.from(mockAdmin, mockStrategy, questionRequestDto);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));

        // When
        QuestionsDetailResponse response = questionService.getQuestionDetail(questionId, adminId, Role.SUPER_ADMIN);

        // Then
        assertNotNull(response);
        assertEquals("문의 제목", response.getTitle());
        assertEquals("문의 내용", response.getQuestionContent());
        verify(questionRepository).findById(questionId);
    }

    @Test
    @DisplayName("문의 상세 조회 실패 - 투자자 권한 없음")
    void getQuestionDetail_Investor_Forbidden() {
        // Given
        Long questionId = 1L;
        Long investorId = 2L; // 실제 호출하는 투자자의 ID

        // 투자자 (다른 사용자로 설정)
        User mockInvestor = mock(User.class);
        when(mockInvestor.getUserId()).thenReturn(3L); // userId 설정
        when(mockInvestor.getNickname()).thenReturn("다른 투자자");
        when(mockInvestor.getRole()).thenReturn(Role.INVESTOR);

        // 트레이더
        User mockTrader = mock(User.class);
        when(mockTrader.getUserId()).thenReturn(4L);
        when(mockTrader.getNickname()).thenReturn("트레이더");
        when(mockTrader.getRole()).thenReturn(Role.TRADER);

        // 전략
        Strategy mockStrategy = Strategy.builder()
                .strategyId(1L)
                .user(mockTrader) // 전략의 트레이더 설정
                .strategyName("전략명")
                .build();

        // 문의 생성
        QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
                .title("문의 제목")
                .content("문의 내용")
                .build();
        Question mockQuestion = Question.from(mockInvestor, mockStrategy, questionRequestDto);

        // Repository Mock 설정
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            questionService.getQuestionDetail(questionId, investorId, Role.INVESTOR);
        });

        assertEquals(ErrorCode.FORBIDDEN_ACCESS, exception.getErrorCode());
        verify(questionRepository).findById(questionId);
    }

    @Test
    @DisplayName("문의 목록 조회 성공 - 빈 결과 반환")
    void getInvestorQuestions_EmptyResult() {
        // Given
        Long userId = 1L;
        String keyword = "문의 제목";
        SearchCondition searchCondition = SearchCondition.TITLE;
        StateCondition stateCondition = StateCondition.ALL;
        String strategyName = "전략명";
        String traderName = "트레이더명";
        Pageable pageable = PageRequest.of(0, 10);
        Role role = Role.INVESTOR;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(questionRepository.searchQuestions(
                userId, keyword, searchCondition, stateCondition, role, pageable, strategyName, traderName, null
        )).thenReturn(Page.empty());

        // When
        QuestionsPageResponse response = questionService.getInvestorQuestions(
                userId, keyword, searchCondition, stateCondition, strategyName, traderName, pageable, role
        );

        // Then
        assertNotNull(response);
        assertEquals(0, response.getPage().getContent().size());
        verify(questionRepository).searchQuestions(
                userId, keyword, searchCondition, stateCondition, role, pageable, strategyName, traderName, null
        );
    }
}