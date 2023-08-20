package com.glimps.glimpsserver.review.infra;

import java.util.Optional;
import java.util.UUID;

import com.glimps.glimpsserver.review.domain.ReviewHeart;

public interface ReviewHeartCustomRepository {

	Optional<ReviewHeart> findByReviewAndUser(UUID reviewUuid, Long userId);
}
