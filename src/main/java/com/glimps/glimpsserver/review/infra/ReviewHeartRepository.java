package com.glimps.glimpsserver.review.infra;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.glimps.glimpsserver.review.domain.ReviewHeart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewHeartRepository extends JpaRepository<ReviewHeart, Long> {

  @Query("select r from ReviewHeart r where r.review.id = :review_id and r.user.id = :user_id")
  Optional<ReviewHeart> findByReviewAndUser(@Param("review_id") UUID reviewUuid, @Param("user_id") Long userId);
}
