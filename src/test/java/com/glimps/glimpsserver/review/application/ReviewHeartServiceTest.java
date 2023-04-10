package com.glimps.glimpsserver.review.application;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewHeart;
import com.glimps.glimpsserver.review.infra.ReviewHeartCustomRepository;
import com.glimps.glimpsserver.review.infra.ReviewHeartRepository;
import com.glimps.glimpsserver.user.domain.RoleType;
import com.glimps.glimpsserver.user.domain.User;

@SpringBootTest
class ReviewHeartServiceTest {
	private static final String TITLE = "제목입니다.";
	private static final String BODY = "본문입니다.";
	private static final String EXISTS_EMAIL = "exists@email.com";
	private static final Long EXISTS_REVIEW_ID = 3L;
	private static final Long EXISTS_USER_ID = 3L;
	private static final UUID EXISTS_REVIEW_UUID = UUID.randomUUID();
	private static final Review EXISTS_REVIEW1 = Review.builder()
		.id(EXISTS_REVIEW_ID)
		.uuid(EXISTS_REVIEW_UUID)
		.title(TITLE)
		.body(BODY)
		.heartsCnt(5)
		.overallRatings(3)
		.longevityRatings(3)
		.sillageRatings(3)
		.build();

	private static final Review EXISTS_REVIEW2 = Review.builder()
		.id(EXISTS_REVIEW_ID)
		.uuid(EXISTS_REVIEW_UUID)
		.title(TITLE)
		.body(BODY)
		.heartsCnt(5)
		.overallRatings(3)
		.longevityRatings(3)
		.sillageRatings(3)
		.build();

	private static final User EXISTS_USER = User.builder()
		.id(EXISTS_USER_ID)
		.email(EXISTS_EMAIL)
		.nickname("test_nickname")
		.role(RoleType.USER)
		.build();

	private final ReviewHeartRepository reviewHeartRepository = mock(ReviewHeartRepository.class);
	private final ReviewHeartCustomRepository reviewHeartCustomRepository = mock(ReviewHeartCustomRepository.class);
	private ReviewHeartService reviewHeartService;

	@BeforeEach
	void setUp() {
		reviewHeartService = new ReviewHeartService(reviewHeartRepository, reviewHeartCustomRepository);
	}

	@Nested
	@DisplayName("createReviewHeart 메서드는")
	class Describe_createReviewHeart {
		@Nested
		@DisplayName("리뷰와 사용자가 주어지면")
		class Context_with_valid_review_heart {
			@BeforeEach
			void setUp() {
				given(reviewHeartRepository.save(any(ReviewHeart.class)))
					.willReturn(ReviewHeart.builder()
						.review(EXISTS_REVIEW1)
						.user(EXISTS_USER)
						.build());
			}

			@Test
			@DisplayName("리뷰 하트를 생성한다")
			void It_creates_review_heart() {
				ReviewHeart reviewHeart = reviewHeartService.createReviewHeart(EXISTS_REVIEW1, EXISTS_USER);

				assertThat(reviewHeart.getReview().getTitle()).isEqualTo(TITLE);
				assertThat(reviewHeart.getUser().getEmail()).isEqualTo(EXISTS_EMAIL);
				assertThat(EXISTS_REVIEW1.getHeartsCnt()).isEqualTo(6);
			}
		}
	}

	@Nested
	@DisplayName("cancelReviewHeart 메서드는")
	class Describe_cancelReviewHeart {
		@Nested
		@DisplayName("리뷰 하트가 존재하면")
		class Context_with_valid_review_heart {
			private final ReviewHeart reviewHeart = ReviewHeart.builder()
				.review(EXISTS_REVIEW2)
				.user(EXISTS_USER)
				.build();

			@BeforeEach
			void setUp() {
				given(reviewHeartCustomRepository.findByReviewAndUser(EXISTS_REVIEW_UUID, EXISTS_USER_ID))
					.willReturn(Optional.of(reviewHeart));
			}

			@Test
			@DisplayName("리뷰 하트를 취소한다")
			void It_cancels_review_heart() {
				reviewHeartService.cancelReviewHeart(EXISTS_REVIEW2, EXISTS_USER);

				assertThat(EXISTS_REVIEW2.getHeartsCnt()).isEqualTo(4);
			}
		}
	}

}
