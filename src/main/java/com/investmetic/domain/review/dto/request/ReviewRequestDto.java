package com.investmetic.domain.review.dto.request;

import com.investmetic.domain.review.model.entity.Review;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.user.model.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewRequestDto {
    @NotNull
    private String content;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer starRating;


    @Builder
    public ReviewRequestDto(String content, Integer starRating) {
        this.content = content;
        this.starRating = starRating;
    }

    public Review toEntity(User user, Strategy strategy){
        return Review.createReview(strategy,user,content,starRating);
    }
}
