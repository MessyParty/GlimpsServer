package com.glimps.glimpsserver.review.dto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewPhoto;
import com.glimps.glimpsserver.user.domain.RoleType;
import com.glimps.glimpsserver.user.domain.User;

class ReviewPageResponseTest {
	private static final Brand TEST_BRAND = Brand.builder()
			.brandNameEng("chanel")
			.brandNameKor("샤넬")
			.build();
	private static final String TEST_PERFUME_NAME = "testPerfumeName";
	private static final String TEST_NICKNAME = "testNickname";
	private static final String TEST_EMAIL = "test@email.com";
	private static final String TEST_TITLE = "testTitle";
	private static final String TEST_BODY = "testBody";
	private static final String TEST_PHOTO_URL1 = "testPhotoUrl1";
	private static final String TEST_PHOTO_URL2 = "testPhotoUrl2";
	private static final String TEST_PHOTO_URL3 = "testPhotoUrl3";

	private static final Perfume PERFUME = Perfume.builder()
		.id(1L)
		.brand(TEST_BRAND)
		.perfumeName(TEST_PERFUME_NAME)
		.build();

	private static final User USER = User.builder()
		.id(1L)
		.reviewCnt(0)
		.role(RoleType.USER)
		.email(TEST_EMAIL)
		.nickname(TEST_NICKNAME)
		.build();

	private static final Review REVIEW = Review.builder()
		.id(1L)
		.title(TEST_TITLE)
		.body(TEST_BODY)
		.user(USER)
		.overallRatings(5.0)
		.longevityRatings(4.5)
		.sillageRatings(4.0)
		.perfume(PERFUME)
		.build();

	private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
	private static final Page<Review> REVIEW_PAGE = new PageImpl<>(List.of(REVIEW), PAGE_REQUEST, 1);

	private static final ReviewPhoto photo1 = ReviewPhoto.createReviewPhoto(REVIEW, TEST_PHOTO_URL1);
	private static final ReviewPhoto photo2 = ReviewPhoto.createReviewPhoto(REVIEW, TEST_PHOTO_URL2);
	private static final ReviewPhoto photo3 = ReviewPhoto.createReviewPhoto(REVIEW, TEST_PHOTO_URL3);

	@Test
	void createReviewPageResponse() {
		List<ReviewPageResponse> reviewPageResponse = ReviewPageResponse.of(REVIEW_PAGE);

		assertThat(REVIEW.getReviewPhotos()).isNotEmpty();
		assertThat(REVIEW.getReviewPhotos()).hasSize(3);
		assertThat(reviewPageResponse.get(0).getPhotoUrl()).hasSize(3);
		assertThat(reviewPageResponse.get(0).getPhotoUrl()).containsExactly(TEST_PHOTO_URL1, TEST_PHOTO_URL2,
			TEST_PHOTO_URL3);
	}
}
