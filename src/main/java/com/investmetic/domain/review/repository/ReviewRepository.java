package com.investmetic.domain.review.repository;

import com.investmetic.domain.review.model.entity.Review;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    int countByStrategy(Strategy strategy);

    Page<Review> findByStrategy(Strategy strategy, Pageable pageable);

    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.strategy.strategyId = :strategyId AND r.user.userId = :userId")
    Boolean existMyReview(@Param("strategyId") Long strategyId, @Param("userId") Long userId);
    List<Review> findAllByUserUserId(Long userId);

    List<Review> findAllByStrategy(Strategy strategy);

}
