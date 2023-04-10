package com.glimps.glimpsserver.review.domain;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.Test;

import com.glimps.glimpsserver.user.domain.User;

class ReviewHeartTest {

	@Test
	void createTest() {
		// given
		Review review = Review.builder().build();
		User user = User.builder().build();

		// when
		ReviewHeart reviewHeart = ReviewHeart.createReviewHeart(review, user);

		// then
		assertThat(reviewHeart).isNotNull();
		assertThat(reviewHeart.getUser()).isEqualTo(user);
		assertThat(reviewHeart.getReview()).isEqualTo(review);
		assertThat(review.getHeartsCnt()).isEqualTo(1);
	}

	@Test
	void cancelTest() {
		// given
		Review review = Review.builder().build();
		User user = User.builder().build();
		ReviewHeart reviewHeart = ReviewHeart.createReviewHeart(review, user);

		// when
		reviewHeart.cancelReviewHeart();

		// then
		assertThat(review.getHeartsCnt()).isZero();
	}

}
