package com.glimps.glimpsserver.review.dto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.Test;

import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.review.domain.Review;

class ReviewCreateResponseTest {

	private static final Perfume PERFUME = Perfume.builder()
		.id(1L)
		.brand("brand")
		.perfumeName("perfumeName")
		.build();

	private static final Review REVIEW = Review.builder()
		.id(1L)
		.title("title")
		.body("body")
		.overallRating(5.0)
		.longevityRating(4.5)
		.sillageRating(4.0)
		.perfume(PERFUME)
		.build();

	@Test
	void createReviewCreateResponse() {
		ReviewCreateResponse reviewCreateResponse = ReviewCreateResponse.of(REVIEW);

		assertThat(reviewCreateResponse.getTitle()).isEqualTo("title");
		assertThat(reviewCreateResponse.getBody()).isEqualTo("body");

	}

}
