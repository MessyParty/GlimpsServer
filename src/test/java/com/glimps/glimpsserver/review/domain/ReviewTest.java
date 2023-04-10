package com.glimps.glimpsserver.review.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.review.dto.ReviewCreateRequest;
import com.glimps.glimpsserver.user.domain.RoleType;
import com.glimps.glimpsserver.user.domain.User;

class ReviewTest {
	private static final String TITLE = "제목입니다.";
	private static final String BODY = "본문입니다.";
	private static final double OVERALL_RATING = 5.0;
	private static final double LONGEVITY_RATING = 4.5;
	private static final double SILLAGE_RATING = 4.0;
	private static final Brand TEST_BRAND = Brand.builder()
			.brandNameEng("chanel")
			.brandNameKor("샤넬")
			.build();

	private static final User user = User.builder()
		.id(5L)
		.nickname("nickname")
		.email("email")
		.reviewCnt(0)
		.role(RoleType.USER)
		.build();

	private static final Perfume perfume = Perfume.createPerfume(TEST_BRAND, "No.5");

	@Test
	void createReview() {
		ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
			.title(TITLE)
			.body(BODY)
			.overallRatings(OVERALL_RATING)
			.longevityRatings(LONGEVITY_RATING)
			.sillageRatings(SILLAGE_RATING)
			.build();

		Review review = Review.createReview(reviewCreateRequest, user, perfume);

		assertThat(review.getTitle()).isEqualTo(TITLE);
		assertThat(review.getBody()).isEqualTo(BODY);
		assertThat(review.getHeartsCnt()).isZero();
		assertThat(review.getPerfume().getPerfumeName()).isEqualTo("No.5");
		assertThat(review.getPerfume().getBrand()).isEqualTo(TEST_BRAND);
		assertThat(review.getOverallRatings()).isEqualTo(OVERALL_RATING);
		assertThat(review.getSillageRatings()).isEqualTo(SILLAGE_RATING);
		assertThat(review.getLongevityRatings()).isEqualTo(LONGEVITY_RATING);
	}
}
