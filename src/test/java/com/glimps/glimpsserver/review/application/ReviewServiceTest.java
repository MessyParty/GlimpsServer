package com.glimps.glimpsserver.review.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.uuid.Generators;
import com.glimps.glimpsserver.common.error.CustomException;
import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.perfume.application.PerfumeService;
import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewHeart;
import com.glimps.glimpsserver.review.dto.ReviewCreateRequest;
import com.glimps.glimpsserver.review.dto.ReviewPageParam;
import com.glimps.glimpsserver.review.infra.ReviewCustomRepository;
import com.glimps.glimpsserver.review.infra.ReviewRepository;
import com.glimps.glimpsserver.user.application.UserService;
import com.glimps.glimpsserver.user.domain.RoleType;
import com.glimps.glimpsserver.user.domain.User;


@Transactional
@SpringBootTest
class ReviewServiceTest {
	private static final String TITLE = "제목입니다.";
	private static final String BODY = "본문입니다.";
	private static final String EXISTS_EMAIL = "exists@email.com";
	private static final String NOT_EXISTS_EMAIL = "notexists@email.com";
	private static final Long EXISTS_PERFUME_ID = 3L;
	private static final UUID EXISTS_PERFUME_UUID = Generators.timeBasedGenerator().generate();
	private static final UUID NOT_EXISTS_PERFUME_ID = Generators.timeBasedGenerator().generate();
	private static final Long NEW_REVIEW_ID = 1L;
	private static final UUID NEW_REVIEW_UUID = UUID.randomUUID();
	private static final Long EXISTS_REVIEW_ID = 3L;
	private static final UUID EXISTS_REVIEW_UUID = UUID.randomUUID();
	private static final UUID NOT_EXISTS_REVIEW_UUID = UUID.randomUUID();
	private static final UUID NOT_EXISTS_PERFUME_UUID = UUID.randomUUID();
	private static final Brand TEST_BRAND = Brand.builder()
			.brandNameEng("chanel")
			.brandNameKor("샤넬")
			.build();
	private static final Perfume EXISTS_PERFUME = Perfume.builder()
		.id(EXISTS_PERFUME_ID)
		.uuid(EXISTS_PERFUME_UUID)
		.perfumeName("향수 이름")
		.brand(TEST_BRAND)
		.build();

	private static final Review EXISTS_REVIEW = Review.builder()
		.id(EXISTS_REVIEW_ID)
		.uuid(EXISTS_REVIEW_UUID)
		.title(TITLE)
		.body(BODY)
		.perfume(EXISTS_PERFUME)
		.heartsCnt(5)
		.overallRatings(3)
		.longevityRatings(3)
		.sillageRatings(3)
		.build();

	private static final User EXISTS_USER = User.builder()
		.id(1L)
		.email(EXISTS_EMAIL)
		.nickname("test_nickname")
		.role(RoleType.USER)
		.build();

	private final ReviewCustomRepository reviewCustomRepository = mock(ReviewCustomRepository.class);
	private final ReviewRepository reviewRepository = mock(ReviewRepository.class);
	private final UserService userService = mock(UserService.class);
	private ReviewService reviewService;
	@MockBean
	private ReviewPhotoService reviewPhotoService;
	@MockBean
	private PerfumeService perfumeService;
	@MockBean
	private ReviewHeartService reviewHeartService;

	@BeforeEach
	void setUp() {
		reviewService = new ReviewService(reviewCustomRepository, reviewRepository, userService, reviewPhotoService,
			perfumeService, reviewHeartService);
	}

	@Nested
	@DisplayName("createReview 메소드는")
	class Describe_createReview {
		@Nested
		@DisplayName("사용자가 존재하고, 향수가 존재할 때")
		class Context_when_user_exists_and_perfume_exists {
			@BeforeEach
			void setUp() {
				given(userService.getUserByEmail(EXISTS_EMAIL)).willReturn(EXISTS_USER);
				given(perfumeService.getPerfumeById(EXISTS_PERFUME_UUID)).willReturn(EXISTS_PERFUME);
				given(reviewRepository.save(any(Review.class))).will(invocation -> {
					Review source = invocation.getArgument(0);
					return Review.builder()
						.id(NEW_REVIEW_ID)
						.uuid(NEW_REVIEW_UUID)
						.title(source.getTitle())
						.body(source.getBody())
						.overallRatings(source.getOverallRatings())
						.longevityRatings(source.getLongevityRatings())
						.sillageRatings(source.getSillageRatings())
						.user(source.getUser())
						.perfume(source.getPerfume())
						.build();
				});
			}

			@Test
			@DisplayName("리뷰를 생성한다.")
			void It_creates_review() {
				ReviewCreateRequest reviewCreateRequest =
					ReviewCreateRequest.builder()
						.title(TITLE)
						.body(BODY)
						.overallRatings(5.0)
						.longevityRatings(4.5)
						.sillageRatings(4.0)
						.build();

				Review review = reviewService.createReview(reviewCreateRequest, EXISTS_EMAIL);

				assertThat(review.getTitle()).isEqualTo(TITLE);
				assertThat(review.getBody()).isEqualTo(BODY);
				assertThat(review.getUuid()).isEqualTo(NEW_REVIEW_UUID);
				assertThat(review.getOverallRatings()).isEqualTo(5.0);
				assertThat(review.getLongevityRatings()).isEqualTo(4.5);
				assertThat(review.getSillageRatings()).isEqualTo(4.0);
				assertThat(review.getUser()).isEqualTo(EXISTS_USER);
			}
		}

		@Nested
		@DisplayName("사용자가 존재하지 않고, 향수가 존재할 때")
		class Context_when_user_not_exists_and_perfume_exists {
			@BeforeEach
			void setUp() {
				given(userService.getUserByEmail(NOT_EXISTS_EMAIL)).willThrow(EntityNotFoundException.class);
			}

			@Test
			@DisplayName("UserNotFoundException을 던진다.")
			void It_throws_UserNotFoundException() {
				ReviewCreateRequest reviewCreateRequest =
					ReviewCreateRequest.builder()
						.title(TITLE)
						.body(BODY)
						.perfumeUuid(EXISTS_PERFUME_UUID)
						.overallRatings(5.0)
						.longevityRatings(4.5)
						.sillageRatings(4.0)
						.build();

				assertThatThrownBy(() -> reviewService.createReview(reviewCreateRequest, NOT_EXISTS_EMAIL))
					.isInstanceOf(EntityNotFoundException.class);
			}
		}

		@Nested
		@DisplayName("사용자가 존재하고, 향수가 존재하지 않을 때")
		class Context_when_user_exists_and_perfume_not_exists {

			@BeforeEach
			void setUp() {
				given(userService.getUserByEmail(EXISTS_EMAIL)).willReturn(EXISTS_USER);
				given(perfumeService.getPerfumeById(NOT_EXISTS_PERFUME_ID)).willThrow(EntityNotFoundException.class);
			}

			@Test
			@DisplayName("PerfumeNotFoundException 을 던진다.")
			void It_throws_PerfumeNotFoundException() {
				ReviewCreateRequest reviewCreateRequest =
					ReviewCreateRequest.builder()
						.title(TITLE)
						.body(BODY)
						.perfumeUuid(NOT_EXISTS_PERFUME_ID)
						.overallRatings(5.0)
						.longevityRatings(4.5)
						.sillageRatings(4.0)
						.build();

				assertThatThrownBy(() -> reviewService.createReview(reviewCreateRequest, EXISTS_EMAIL))
					.isInstanceOf(EntityNotFoundException.class);
			}
		}
	}

	@Nested
	@DisplayName("getReview 메소드는")
	class Describe_getReview {
		@Nested
		@DisplayName("리뷰가 존재할 때")
		class Context_when_review_exists {
			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findByUuid(EXISTS_REVIEW_UUID)).willReturn(Optional.of(EXISTS_REVIEW));
			}

			@Test
			@DisplayName("리뷰를 반환한다.")
			void It_returns_review() {
				Review review = reviewService.getReviewById(EXISTS_REVIEW_UUID);

				assertThat(review.getId()).isEqualTo(EXISTS_REVIEW_ID);
				assertThat(review.getUuid()).isEqualTo(EXISTS_REVIEW_UUID);
				assertThat(review.getTitle()).isEqualTo(TITLE);
				assertThat(review.getBody()).isEqualTo(BODY);
				assertThat(review.getLongevityRatings()).isEqualTo(3);
				assertThat(review.getSillageRatings()).isEqualTo(3);
				assertThat(review.getOverallRatings()).isEqualTo(3);

			}
		}

		@Nested
		@DisplayName("리뷰가 존재하지 않을 때")
		class Context_when_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findByUuid(NOT_EXISTS_REVIEW_UUID)).willReturn(Optional.empty());
			}

			@Test
			@DisplayName("ReviewNotFoundException을 던진다.")
			void It_throws_ReviewNotFoundException() {
				assertThatThrownBy(() -> reviewService.getReviewById(NOT_EXISTS_REVIEW_UUID))
					.isInstanceOf(EntityNotFoundException.class);
			}
		}
	}

	@Nested
	@DisplayName("getMyReviews 메소드는")
	class Describe_getMyReviews {
		private final ReviewPageParam reviewPageParam = new ReviewPageParam(0, 2, null, null);
		private final Pageable pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "createdAt");
		private final UUID ELEMENT1_UUID = UUID.randomUUID();
		private final UUID ELEMENT2_UUID = UUID.randomUUID();
		private final UUID ELEMENT3_UUID = UUID.randomUUID();

		Review element1 = Review.builder()
			.id(5L)
			.uuid(ELEMENT1_UUID)
			.user(EXISTS_USER)
			.title("element1 title")
			.body("element1 body")
			.build();

		Review element2 = Review.builder()
			.id(6L)
			.uuid(ELEMENT2_UUID)
			.user(EXISTS_USER)
			.title("element2 title")
			.body("element2 body")
			.build();

		Review element3 = Review.builder()
			.id(7L)
			.uuid(ELEMENT3_UUID)
			.user(EXISTS_USER)
			.title("element3 title")
			.body("element3 body")
			.build();

		@Nested
		@DisplayName("사용자가 존재하고, 사용자가 작성한 리뷰가 존재할 때")
		class Context_when_user_exists_and_review_exists {
			@BeforeEach
			void setUp() {
				PageImpl<Review> customPage = new PageImpl<>(
					List.of(EXISTS_REVIEW, element1, element2, element3), pageable, 4);
				given(userService.getUserByEmail(EXISTS_EMAIL)).willReturn(EXISTS_USER);
				given(reviewCustomRepository.findAllByUserId(EXISTS_USER.getId(), pageable)).willReturn(customPage);
			}

			@Test
			@DisplayName("사용자가 작성한 리뷰 목록을 반환한다.")
			void It_returns_review_list() {
				Page<Review> reviews = reviewService.getMyReviews(reviewPageParam, EXISTS_EMAIL);

				assertThat(reviews.getTotalElements()).isEqualTo(4);
				assertThat(reviews.getContent()).contains(EXISTS_REVIEW, element1, element2, element3);
				assertThat(reviews.getTotalPages()).isEqualTo(2);
			}
		}

		@Nested
		@DisplayName("사용자가 존재하고, 사용자가 작성한 리뷰가 존재하지 않을 때")
		class Context_when_user_exists_and_review_not_exists {
			@BeforeEach
			void setUp() {
				given(userService.getUserByEmail(EXISTS_EMAIL)).willReturn(EXISTS_USER);
				given(reviewCustomRepository.findAllByUserId(EXISTS_USER.getId(), pageable)).willReturn(
					Page.empty());
			}

			@Test
			@DisplayName("빈 페이지를 반환한다.")
			void It_returns_empty_list() {
				Page<Review> reviews = reviewService.getMyReviews(reviewPageParam, EXISTS_EMAIL);

				assertThat(reviews.getContent()).isEmpty();
			}
		}

		@Nested
		@DisplayName("사용자가 존재하지 않을 때")
		class Context_when_user_not_exists {
			@BeforeEach
			void setUp() {
				given(userService.getUserByEmail(NOT_EXISTS_EMAIL)).willThrow(EntityNotFoundException.class);
			}

			@Test
			@DisplayName("UserNotFoundException 을 던진다.")
			void It_throws_UserNotFoundException() {
				assertThatThrownBy(() -> reviewService.getMyReviews(reviewPageParam, NOT_EXISTS_EMAIL))
					.isInstanceOf(CustomException.class);
			}
		}
	}

	@Nested
	@DisplayName("getRecentReviews 메소드는")
	class Describe_getRecentReviews {
		@Nested
		@DisplayName("리뷰가 존재하면")
		class Context_when_review_exists {
			private final UUID ELEMENT1_UUID = UUID.randomUUID();
			private final UUID ELEMENT2_UUID = UUID.randomUUID();
			private final UUID ELEMENT3_UUID = UUID.randomUUID();

			Review element1 = Review.builder()
				.id(5L)
				.uuid(ELEMENT1_UUID)
				.user(EXISTS_USER)
				.title("element1 title")
				.body("element1 body")
				.build();

			Review element2 = Review.builder()
				.id(6L)
				.uuid(ELEMENT2_UUID)
				.user(EXISTS_USER)
				.title("element2 title")
				.body("element2 body")
				.build();

			Review element3 = Review.builder()
				.id(7L)
				.uuid(ELEMENT3_UUID)
				.user(EXISTS_USER)
				.title("element3 title")
				.body("element3 body")
				.build();

			@BeforeEach
			void setUp() {
				List<Review> result = List.of(element1, element2, element3);
				given(reviewCustomRepository.findTop10ByOrderByCreatedAtDesc())
					.willReturn(result);
			}

			@Test
			@DisplayName("리뷰를 최대 10개 반환한다.")
			void It_returns_max_10_reviews() {
				List<Review> reviews = reviewService.getRecentReviews();

				assertThat(reviews).hasSize(3);
				verify(reviewCustomRepository).findTop10ByOrderByCreatedAtDesc();
			}
		}

		@Nested
		@DisplayName("리뷰가 존재하지 않으면")
		class Context_when_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findTop10ByOrderByCreatedAtDesc()).willReturn(List.of());
			}

			@Test
			@DisplayName("빈 리스트를 반환한다.")
			void It_returns_empty_list() {
				List<Review> recentReviews = reviewService.getRecentReviews();

				assertThat(recentReviews).isEmpty();
				verify(reviewCustomRepository).findTop10ByOrderByCreatedAtDesc();
			}
		}
	}

	@Nested
	@DisplayName("createHeart 메서드는")
	class Describe_createHeart {
		@Nested
		@DisplayName("리뷰가 존재하고, 사용자가 존재하고, 사용자가 리뷰에 좋아요를 누를 때")
		class Context_when_review_exists_and_user_exists_and_user_click_heart {
			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findByUuid(EXISTS_REVIEW.getUuid())).willReturn(
					Optional.of(EXISTS_REVIEW));
				given(userService.getUserByEmail(EXISTS_EMAIL)).willReturn(EXISTS_USER);
				given(reviewHeartService.createReviewHeart(any(Review.class), any(User.class))).will(invocation -> {
					Review review = invocation.getArgument(0);
					User user = invocation.getArgument(1);
					return ReviewHeart.createReviewHeart(review, user);
				});

			}

			@Test
			@DisplayName("리뷰에 좋아요 수를 증가시킨다.")
			void It_increase_heart_cnt() {
				int originalReviewCnt = EXISTS_REVIEW.getHeartsCnt();
				Review review = reviewService.createHeart(EXISTS_REVIEW.getUuid(), EXISTS_EMAIL);

				assertThat(review.getHeartsCnt()).isEqualTo(originalReviewCnt + 1);

				verify(reviewCustomRepository).findByUuid(EXISTS_REVIEW.getUuid());
				verify(userService).getUserByEmail(EXISTS_EMAIL);
				verify(reviewHeartService).createReviewHeart(any(Review.class), any(User.class));
			}
		}

		@Nested
		@DisplayName("리뷰가 존재하지 않을 때")
		class Context_when_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findByUuid(NOT_EXISTS_REVIEW_UUID)).willReturn(
					Optional.empty());
			}

			@Test
			@DisplayName("ReviewNotFoundException 을 던진다.")
			void It_throws_ReviewNotFoundException() {
				assertThatThrownBy(() -> reviewService.createHeart(NOT_EXISTS_REVIEW_UUID, EXISTS_EMAIL))
					.isInstanceOf(EntityNotFoundException.class);
			}
		}

		@Nested
		@DisplayName("사용자가 존재하지 않을 때")
		class Context_when_user_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findByUuid(EXISTS_REVIEW.getUuid())).willReturn(
					Optional.of(EXISTS_REVIEW));
				given(userService.getUserByEmail(NOT_EXISTS_EMAIL)).willThrow(EntityNotFoundException.class);
			}

			@Test
			@DisplayName("UserNotFoundException 을 던진다.")
			void It_throws_UserNotFoundException() {
				assertThatThrownBy(() -> reviewService.createHeart(EXISTS_REVIEW_UUID, NOT_EXISTS_EMAIL))
					.isInstanceOf(EntityNotFoundException.class);
			}
		}
	}

	@Nested
	@DisplayName("cancelHeart 메서드는")
	class Describe_cancelHeart {
		@Nested
		@DisplayName("리뷰가 존재하고, 사용자가 존재하고, 사용자가 리뷰에 좋아요를 취소할 때")
		class Context_when_review_exists_and_user_exists_and_user_cancel_heart {
			private final ReviewHeart EXISTS_REVIEW_HEART = ReviewHeart.builder()
				.id(1L)
				.review(EXISTS_REVIEW)
				.user(EXISTS_USER)
				.build();

			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findByUuid(EXISTS_REVIEW.getUuid())).willReturn(
					Optional.of(EXISTS_REVIEW));
				given(userService.getUserByEmail(EXISTS_EMAIL)).willReturn(EXISTS_USER);
				given(reviewHeartService.cancelReviewHeart(any(Review.class), any(User.class))).will(invocation -> {
					Review review = invocation.getArgument(0);
					review.decreaseHeartCnt();
					return EXISTS_REVIEW_HEART;
				});
			}

			@Test
			@DisplayName("리뷰에 좋아요 수를 감소시킨다.")
			void It_decreases_heart_cnt() {
				int originalReviewCnt = EXISTS_REVIEW.getHeartsCnt();
				Review review = reviewService.cancelHeart(EXISTS_REVIEW.getUuid(), EXISTS_EMAIL);

				assertThat(review.getHeartsCnt()).isEqualTo(originalReviewCnt - 1);

				verify(reviewCustomRepository).findByUuid(EXISTS_REVIEW.getUuid());
				verify(userService).getUserByEmail(EXISTS_EMAIL);
				verify(reviewHeartService).cancelReviewHeart(any(Review.class), any(User.class));
			}
		}
	}

	@Nested
	@DisplayName("getPerfumeReviews 메서드는")
	class Describe_getPerfumeReviews {
		@Nested
		@DisplayName("향수가 존재하고, 향수에 따른 리뷰가 존재할 때")
		class Context_when_perfume_exists_and_review_exists {
			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findAllByPerfumeId(EXISTS_PERFUME.getUuid())).willReturn(
					List.of(EXISTS_REVIEW));
			}

			@Test
			@DisplayName("향수에 따른 리뷰를 반환한다.")
			void It_returns_perfume_reviews() {
				List<Review> reviews = reviewService.getPerfumeReviews(EXISTS_PERFUME.getUuid());

				assertThat(reviews).hasSize(1);
				assertThat(reviews.get(0)).isEqualTo(EXISTS_REVIEW);

				verify(reviewCustomRepository).findAllByPerfumeId(EXISTS_PERFUME.getUuid());
			}
		}

		@Nested
		@DisplayName("향수가 존재하지 않을 때")
		class Context_when_perfume_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findAllByPerfumeId(NOT_EXISTS_PERFUME_UUID)).willReturn(List.of());
			}

			@Test
			@DisplayName("PerfumeNotFoundException 을 던진다.")
			void It_throws_PerfumeNotFoundException() {
				List<Review> reviews = reviewService.getPerfumeReviews(NOT_EXISTS_PERFUME_UUID);

				assertThat(reviews).isEmpty();

				verify(reviewCustomRepository).findAllByPerfumeId(NOT_EXISTS_PERFUME_UUID);
			}
		}
	}

	@Nested
	@DisplayName("getBestReviews 메서드는")
	class Describe_getBestReviews {
		@Nested
		@DisplayName("리뷰가 존재할 때")
		class Context_when_review_exists {
			private final Review review1 = Review.builder()
				.id(1L)
				.perfume(EXISTS_PERFUME)
				.user(EXISTS_USER)
				.heartsCnt(10)
				.build();

			private final Review review2 = Review.builder()
				.id(2L)
				.perfume(EXISTS_PERFUME)
				.user(EXISTS_USER)
				.heartsCnt(5)
				.build();

			private final Review review3 = Review.builder()
				.id(3L)
				.perfume(EXISTS_PERFUME)
				.user(EXISTS_USER)
				.heartsCnt(3)
				.build();

			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findBestReviewByAmount(anyInt())).willReturn(
					List.of(review1, review2, review3)
				);
			}

			@Test
			@DisplayName("좋아요 수 상위 n개의 리뷰를 반환한다.")
			void It_returns_best_reviews() {
				List<Review> bestReviews = reviewService.getBestReviews(3);

				assertThat(bestReviews).hasSize(3);
				assertThat(bestReviews.get(0).getHeartsCnt()).isEqualTo(10);
				assertThat(bestReviews.get(1).getHeartsCnt()).isEqualTo(5);
				assertThat(bestReviews.get(2).getHeartsCnt()).isEqualTo(3);
			}
		}

		@Nested
		@DisplayName("리뷰가 존재하지 않을 때")
		class Context_when_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewCustomRepository.findBestReviewByAmount(anyInt())).willReturn(List.of());
			}

			@Test
			@DisplayName("빈 리스트를 반환한다.")
			void It_returns_empty_list() {
				List<Review> bestReviews = reviewService.getBestReviews(3);

				assertThat(bestReviews).isEmpty();
			}
		}
	}

}
