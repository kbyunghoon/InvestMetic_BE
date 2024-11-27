package com.investmetic.domain.qna.model.entity;

import com.investmetic.domain.qna.dto.request.QuestionRequestDto;
import com.investmetic.domain.qna.model.QnaState;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    private String targetName;      //문의대상 트레이더

    private String title;       //문의제목

    @Column(length = 5000)
    private String content;     //문의내용

    private String strategyName; //전략명    ..?

    @OneToOne(mappedBy = "question", fetch = FetchType.LAZY)
    @Getter
    private Answer answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QnaState qnaState; // 답변 상태

    public static Question from(User user, Strategy strategy, QuestionRequestDto request) {
        Question question = new Question();
        question.user = user;
        question.strategy = strategy;
        question.targetName = strategy.getUser().getNickname();
        question.title = request.getTitle();
        question.qnaState = QnaState.WAITING;
        question.content = request.getContent();
        return question;
    }


    public void updateQnaState(QnaState newState) {
        if (this.qnaState != newState) {
            this.qnaState = newState;
        }

    }
}
