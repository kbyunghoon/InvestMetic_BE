package com.investmetic.domain.qna.service;

import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.dto.response.AdminQuestionListResponseDto;
import com.investmetic.domain.qna.dto.response.InvestorQuestionListResponseDto;
import com.investmetic.domain.qna.dto.response.TraderQuestionListResponseDto;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final StrategyRepository strategyRepository;

    //문의 등록
    @Transactional
    public void createQuestion(Long userId, Long strategyId, QuestionRequestDto questionRequestDto) {

        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        Question question = questionRequestDto.toEntity(user, strategy);

        questionRepository.save(question);

    }

    //문의 삭제

    public void deleteQuestion(Long strategyId, Long questionId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
        Question question = questionRepository.findByStrategyAndQuestionId(strategy, questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        questionRepository.delete(question);
    }
//    //유저가 작성한 모든 문의 삭제 - 회원 탈퇴 or 추방
//    @Transactional
//    public ResponseEntity<BaseResponse<Void>> deleteAllQuestionByUser(Long userId){
//        User user = userRepository.findById(userId)
//                        .orElseThrow(()-> new BusinessException(ErrorCode.USERS_NOT_FOUND));
//
//        List<Question> questions = questionRepository.findAllByUser(user);
//        questionRepository.deleteAll(questions);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    //특정 전략과 관련된 모든 문의 삭제 - 전략 삭제
//    @Transactional
//    public ResponseEntity<BaseResponse<Void>> deleteAllQuestionByStrategy(Long strategyId) {
//        Strategy strategy = strategyRepository.findById(strategyId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
//
//        List<Question> questions = questionRepository.findAllByStrategy(strategy);
//        questionRepository.deleteAll(questions);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    //투자자 문의 목록 조회
    public ResponseEntity<BaseResponse<PageResponseDto<InvestorQuestionListResponseDto>>> getInvestorQuestionList(Long userId, Pageable pageable) {
        // 투자자 유저 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        // 투자자가 작성한 질문 목록 조회
        Page<Question> questions = questionRepository.findByUser(user, pageable);

        // DTO 변환
        Page<InvestorQuestionListResponseDto> response = questions.map(InvestorQuestionListResponseDto::from);

        // 결과 반환
        return BaseResponse.success(new PageResponseDto<>(response));
    }



    //트레이더 문의 목록 조회
    public ResponseEntity<BaseResponse<PageResponseDto<TraderQuestionListResponseDto>>> getTraderQuestionsList(Long strategyId, Pageable pageable) {
        // 전략 검증
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        // 해당 전략에 연결된 질문 목록 조회
        Page<Question> questions = questionRepository.findByStrategy(strategy, pageable);

        // DTO 변환
        Page<TraderQuestionListResponseDto> response = questions.map(TraderQuestionListResponseDto::from);

        // 결과 반환
        return BaseResponse.success(new PageResponseDto<>(response));
    }

    // 관리자 문의 목록 조회
    public ResponseEntity<BaseResponse<PageResponseDto<AdminQuestionListResponseDto>>> getAdminQuestionList(Pageable pageable) {
        Page<Question> questions = questionRepository.findAll(pageable);

        // DTO 변환
        Page<AdminQuestionListResponseDto> response = questions.map(AdminQuestionListResponseDto::from);

        // 성공 응답 반환
        return BaseResponse.success(new PageResponseDto<>(response));
    }
}


