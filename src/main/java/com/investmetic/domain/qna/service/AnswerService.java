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
        if (!question.getStrategy().getUser().getUserId().equals(traderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
        Answer answer = Answer.createAnswer(question, answerRequestDto.getContent());
        answerRepository.save(answer);

        question.updateQnaState(QnaState.COMPLETED);
    }

    //문의 답변 삭제
    @Transactional
    public void deleteAnswer(Long answerId, Long questionId, String role) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        if ("trader".equalsIgnoreCase(role) &&
                !question.getStrategy().getUser().getUserId().equals(answer.getQuestion().getStrategy().getUser().getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 관리자는 권한 검증 없이 삭제 가능
        if (!"trader".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
        answerRepository.delete(answer);

        //문의 답변 삭제시 qna상태 WAITING으로 변경
        question.updateQnaState(QnaState.WAITING);
        questionRepository.save(question);
    }
}

