package com.glimps.glimpsserver.review.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReviewPhotoTest {
	private static final String PHOTO_URL = "url";
	private static final Review review = new Review();

	@Test
	void createReviewPhoto() {
		ReviewPhoto reviewPhoto = ReviewPhoto.createReviewPhoto(review, PHOTO_URL);

		assertThat(reviewPhoto.getUrl()).isEqualTo(PHOTO_URL);
		assertThat(review.getReviewPhotos()).isNotEmpty();
		assertThat(review.getReviewPhotos().get(0).getUrl()).isEqualTo(PHOTO_URL);
	}

}
