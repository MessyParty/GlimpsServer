package com.glimps.glimpsserver.review.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewHeart;
import com.glimps.glimpsserver.review.infra.ReviewHeartCustomRepository;
import com.glimps.glimpsserver.review.infra.ReviewHeartRepository;
import com.glimps.glimpsserver.user.domain.User;

@Service
@Transactional(readOnly = true)
public class ReviewHeartService {
	private final ReviewHeartRepository reviewHeartRepository;
	private final ReviewHeartCustomRepository reviewHeartCustomRepository;

	public ReviewHeartService(ReviewHeartRepository reviewHeartRepository,
		ReviewHeartCustomRepository reviewHeartCustomRepository) {
		this.reviewHeartRepository = reviewHeartRepository;
		this.reviewHeartCustomRepository = reviewHeartCustomRepository;
	}

	@Transactional
	public ReviewHeart createReviewHeart(Review review, User user) {
		ReviewHeart reviewHeart = ReviewHeart.createReviewHeart(review, user);
		return reviewHeartRepository.save(reviewHeart);
	}

	@Transactional
	public ReviewHeart cancelReviewHeart(Review review, User user) {
		ReviewHeart reviewHeart = reviewHeartCustomRepository.findByReviewAndUser(review.getUuid(), user.getId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_HEART_NOT_FOUND, review.getUuid()));
		reviewHeart.cancelReviewHeart();
		reviewHeartRepository.delete(reviewHeart);
		return reviewHeart;
	}
}
