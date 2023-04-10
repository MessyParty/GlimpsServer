package com.glimps.glimpsserver.review.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glimps.glimpsserver.review.domain.ReviewHeart;

public interface ReviewHeartRepository extends JpaRepository<ReviewHeart, Long> {
}
