package com.investmetic.domain.review.repository;

import com.investmetic.domain.review.model.entity.Review;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    int countByStrategy(Strategy strategy);

    Page<Review> findByStrategy(Strategy strategy, Pageable pageable);

    List<Review> findAllByUserUserId(Long userId);

    List<Review> findAllByStrategy(Strategy strategy);

}
