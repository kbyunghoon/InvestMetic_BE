package com.investmetic.domain.qna.service;

import com.investmetic.domain.qna.dto.request.AnswerRequestDto;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.qna.model.entity.Answer;
import com.investmetic.domain.qna.model.entity.Question;
import com.investmetic.domain.qna.repository.AnswerRepository;
import com.investmetic.domain.qna.repository.QuestionRepository;
import com.investmetic.domain.user.model.Role;
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


    //문의 답변 삭제
    @Transactional
    public void deleteAnswer(Long answerId, Long questionId, Role role, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        if (Role.isTrader(role)) {
            Long traderId = question.getStrategy().getUser().getUserId();
            if (!traderId.equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
            }
        } else if (!Role.isAdmin(role)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        answerRepository.delete(answer);

        // 문의 상태를 WAITING으로 업데이트
        question.updateQnaState(QnaState.WAITING);
        questionRepository.save(question);
    }

}

