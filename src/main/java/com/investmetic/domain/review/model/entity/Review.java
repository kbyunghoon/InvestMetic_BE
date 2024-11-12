package com.investmetic.domain.review.model.entity;

import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId; // 단일 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy; // 전략 FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 회원 FK

    private String nickname; //작성자

    @Column(length = 1000)
    private String content; //리뷰내용

    private int starRating; //별점

    public static Review createReview(Strategy strategy, User user, String content, int starRating) {
        Review review = new Review();
        review.strategy = strategy;
        review.user = user;
        review.nickname = user.getNickname();  // User의 닉네임을 가져와 설정
        review.content = content;
        review.starRating = starRating;
        return review;
    }

    // 리뷰 내용과 별점 수정
    public void updateReview(String content, int starRating) {
        this.content = content;
        this.starRating = starRating;
    }

}