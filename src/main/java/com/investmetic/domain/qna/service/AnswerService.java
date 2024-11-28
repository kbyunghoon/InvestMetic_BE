package com.investmetic.domain.qna.service;

import com.investmetic.domain.qna.dto.request.AnswerRequestDto;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.AnswerRepository;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    //문의 답변 등록
    @Transactional
    public void createAnswer(Long questionId, Long traderId, AnswerRequestDto answerRequestDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        if (question.getStrategy() == null
                || question.getStrategy().getUser() == null
                || !question.getStrategy().getUser().getUserId().equals(traderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
        Answer answer = Answer.builder()
                .question(question)
                .content(answerRequestDto.getContent())
                .build();

        answerRepository.save(answer);

        // 문의 상태를 COMPLETED로 업데이트
        question.updateQnaState(QnaState.COMPLETED);
        questionRepository.save(question);
    }


    // 트레이더 전용 답변 삭제
    @Transactional
    public void deleteTraderAnswer(Long answerId, Long questionId, Long traderId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        if (!isTraderAuthorized(question, traderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        deleteAnswer(answer, question);
    }

    // 관리자 전용 답변 삭제
    @Transactional
    public void deleteAdminAnswer(Long answerId, Long questionId) {
        questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        deleteAnswer(answerRepository.findById(answerId).get(), questionRepository.findById(questionId).get());
    }

    // 공통 메서드: 답변 삭제 및 상태 업데이트
    private void deleteAnswer(Answer answer, Question question) {
        answerRepository.delete(answer);
        question.updateQnaState(QnaState.WAITING); // 상태를 WAITING으로 업데이트
        questionRepository.save(question);
    }

    // 권한 확인: 트레이더가 해당 문의에 접근 권한이 있는지 확인
    private boolean isTraderAuthorized(Question question, Long traderId) {
        return question.getStrategy() != null
                && question.getStrategy().getUser() != null
                && question.getStrategy().getUser().getUserId().equals(traderId);
    }

}

