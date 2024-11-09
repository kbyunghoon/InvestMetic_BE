package com.investmetic.domain.review.repository;

import com.investmetic.domain.review.model.entity.Review;
import com.investmetic.domain.strategy.model.entity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    int countByStrategy(Strategy strategy);
}
