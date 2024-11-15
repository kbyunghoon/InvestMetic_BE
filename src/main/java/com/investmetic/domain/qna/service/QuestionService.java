package com.investmetic.domain.qna.service;

import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Void> createQuestion(Long userId, Long strategyId, QuestionRequestDto questionRequestDto) {

        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        Question question = questionRequestDto.toEntity(user, strategy);

        questionRepository.save(question);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //문의 삭제
    @Transactional
    public ResponseEntity<Void> deleteQuestion(Long strategyId, Long questionId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
        Question question = questionRepository.findByStrategyAndQuestionId(strategy, questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        questionRepository.delete(question);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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


}
