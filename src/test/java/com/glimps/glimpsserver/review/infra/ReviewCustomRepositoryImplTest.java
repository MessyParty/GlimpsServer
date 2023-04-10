package com.glimps.glimpsserver.review.infra;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.uuid.Generators;
import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.infra.PerfumeRepository;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.user.domain.RoleType;
import com.glimps.glimpsserver.user.domain.User;
import com.glimps.glimpsserver.user.infra.UserRepository;

@SpringBootTest
@Transactional
class ReviewCustomRepositoryImplTest {
	private static final String TITLE = "제목입니다.";
	private static final String BODY = "본문입니다.";
	private static final String EXISTS_EMAIL = "exists@email.com";
	private static final Long NOT_EXISTS_USER_ID = 200L;
	private static final UUID EXISTS_REVIEW_UUID = Generators.timeBasedGenerator().generate();
	private static final UUID NOT_EXISTS_REVIEW_UUID = UUID.randomUUID();
	private static final UUID NOT_EXISTS_PERFUME_UUID = UUID.randomUUID();
	private static final Brand TEST_BRAND = Brand.builder().brandName("chanel").build();
	private static final Perfume EXISTS_PERFUME = Perfume.builder()
		.uuid(Generators.timeBasedGenerator().generate())
		.perfumeName("향수 이름")
		.brand(TEST_BRAND)
		.build();

	private static final User EXISTS_USER = User.builder()
		.email(EXISTS_EMAIL)
		.nickname("test_nickname")
		.role(RoleType.USER)
		.build();

	private final Review EXISTS_REVIEW = Review.builder()
		.title(TITLE)
		.body(BODY)
		.uuid(EXISTS_REVIEW_UUID)
		.overallRatings(5)
		.longevityRatings(3)
		.sillageRatings(3)
		.heartsCnt(5)
		.perfume(EXISTS_PERFUME)
		.user(EXISTS_USER)
		.build();

	private final Review SECOND_REVIEW = Review.builder()
		.title(TITLE + "2")
		.body(BODY)
		.uuid(Generators.timeBasedGenerator().generate())
		.overallRatings(5)
		.longevityRatings(3)
		.sillageRatings(3)
		.heartsCnt(10)
		.perfume(EXISTS_PERFUME)
		.user(EXISTS_USER)
		.build();

	private final Review THIRD_REVIEW = Review.builder()
		.title(TITLE + "3")
		.body(BODY)
		.uuid(Generators.timeBasedGenerator().generate())
		.overallRatings(5)
		.longevityRatings(3)
		.sillageRatings(3)
		.heartsCnt(7)
		.perfume(EXISTS_PERFUME)
		.user(EXISTS_USER)
		.build();

	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private ReviewCustomRepository reviewCustomRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PerfumeRepository perfumeRepository;

	@Nested
	@DisplayName("findByUuid 메서드는")
	class Describe_findByUuid {
		@Nested
		@DisplayName("존재하는 리뷰의 uuid를 전달하면")
		class Context_when_exists_uuid {
			@Test
			@DisplayName("해당 리뷰를 반환한다.")
			void it_returns_review() {
				// given
				perfumeRepository.save(EXISTS_PERFUME);
				userRepository.save(EXISTS_USER);
				reviewRepository.save(EXISTS_REVIEW);

				//when
				Optional<Review> r = reviewCustomRepository.findByUuid(EXISTS_REVIEW_UUID);

				//then
				assertThat(r).isPresent();

				Review review = r.get();
				assertThat(review.getTitle()).isEqualTo(TITLE);
				assertThat(review.getBody()).isEqualTo(BODY);
			}
		}

		@Nested
		@DisplayName("존재하지 않는 리뷰의 uuid를 전달하면")
		class Context_when_not_exists_uuid {
			@Test
			@DisplayName("빈 값을 반환한다.")
			void it_returns_empty() {

				//when
				Optional<Review> r = reviewCustomRepository.findByUuid(NOT_EXISTS_REVIEW_UUID);

				//then
				assertThat(r).isEmpty();
			}
		}
	}

	@Nested
	@DisplayName("findBestReviewByAmount 메서드는")
	class Describe_findBestReviewByAmount {

		@Nested
		@DisplayName("리뷰가 존재하면")
		class Context_when_review_exists {
			@Test
			@DisplayName("주어진 갯수 만큼의 베스트 리뷰를 반한다.")
			void it_returns_best_review() {
				//given
				perfumeRepository.save(EXISTS_PERFUME);
				userRepository.save(EXISTS_USER);
				reviewRepository.save(EXISTS_REVIEW);
				reviewRepository.save(SECOND_REVIEW);
				reviewRepository.save(THIRD_REVIEW);

				//when
				List<Review> reviews = reviewCustomRepository.findBestReviewByAmount(2);

				//then
				assertThat(reviews).hasSize(2);
				assertThat(reviews.get(0).getHeartsCnt()).isEqualTo(10);
				assertThat(reviews.get(1).getHeartsCnt()).isEqualTo(7);
			}
		}

		@Nested
		@DisplayName("리뷰가 존재하지 않으면")
		class Context_when_review_not_exists {
			@Test
			@DisplayName("빈 리스트를 반환한다.")
			void it_returns_empty_list() {
				//given
				perfumeRepository.save(EXISTS_PERFUME);
				userRepository.save(EXISTS_USER);

				//when
				List<Review> reviews = reviewCustomRepository.findBestReviewByAmount(3);

				//then
				assertThat(reviews).isEmpty();
			}
		}
	}

	@Nested
	@DisplayName("findAllByPerfumeId 메서드는")
	class Describe_findAllByPerfumeId {
		@Nested
		@DisplayName("존재하는 향수의 id를 전달하면")
		class Context_when_exists_perfume_uuid {
			@Test
			@DisplayName("해당 향수의 리뷰를 반환한다.")
			void it_returns_review() {
				//given
				Perfume perfume = perfumeRepository.save(EXISTS_PERFUME);
				userRepository.save(EXISTS_USER);
				reviewRepository.save(EXISTS_REVIEW);
				reviewRepository.save(SECOND_REVIEW);

				//when
				List<Review> reviews = reviewCustomRepository.findAllByPerfumeId(perfume.getUuid());

				//then
				assertThat(reviews).hasSize(2);

				// 최신 순으로 정렬
				assertThat(reviews.get(0).getTitle()).isEqualTo(TITLE + "2");
				assertThat(reviews.get(1).getTitle()).isEqualTo(TITLE);
			}
		}

		@Nested
		@DisplayName("존재하지 않는 향수의 id를 전달하면")
		class Context_when_not_exists_perfume_uuid {
			@Test
			@DisplayName("빈 리스트를 반환한다.")
			void it_returns_empty_list() {
				//given
				perfumeRepository.save(EXISTS_PERFUME);
				userRepository.save(EXISTS_USER);
				reviewRepository.save(EXISTS_REVIEW);

				//when
				List<Review> reviews = reviewCustomRepository.findAllByPerfumeId(NOT_EXISTS_PERFUME_UUID);

				//then
				assertThat(reviews).isEmpty();
			}
		}
	}

	@Nested
	@DisplayName("findAllByUserId 메서드는")
	class Describe_findAllByUserId {
		@Nested
		@DisplayName("존재하는 유저의 id를 전달하면")
		class Context_when_exists_user_id {
			@Test
			@DisplayName("해당 유저의 리뷰를 반환한다.")
			void It_returns_users_reviews() {
				//given
				perfumeRepository.save(EXISTS_PERFUME);
				userRepository.save(EXISTS_USER);
				reviewRepository.save(EXISTS_REVIEW);
				reviewRepository.save(SECOND_REVIEW);

				//when
				Pageable pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
				Page<Review> reviews = reviewCustomRepository.findAllByUserId(EXISTS_USER.getId(), pageRequest);

				//then
				assertThat(reviews.getContent()).hasSize(2);
				assertThat(reviews.getTotalPages()).isEqualTo(1);
			}
		}

		@Nested
		@DisplayName("존재하지 않는 유저의 id를 전달하면")
		class Context_when_not_exists_user_id {
			@Test
			@DisplayName("빈 리스트를 반환한다.")
			void It_returns_empty_list() {
				//given
				perfumeRepository.save(EXISTS_PERFUME);
				userRepository.save(EXISTS_USER);
				reviewRepository.save(EXISTS_REVIEW);
				reviewRepository.save(SECOND_REVIEW);

				//when
				Pageable pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
				Page<Review> reviews = reviewCustomRepository.findAllByUserId(NOT_EXISTS_USER_ID, pageRequest);

				//then
				assertThat(reviews.getContent()).isEmpty();
				assertThat(reviews.getTotalPages()).isZero();
			}
		}
	}

	@Nested
	@DisplayName("findAllByOrder 메서드는")
	class Describe_findAllByOrder {
		@Nested
		@DisplayName("페이지 정보를 전달하면")
		class Context_when_page_info {
			@Test
			@DisplayName("페이지 정보에 맞는 리뷰를 반환한다.")
			void it_returns_reviews() {
				//given
				perfumeRepository.save(EXISTS_PERFUME);
				userRepository.save(EXISTS_USER);
				reviewRepository.save(EXISTS_REVIEW);
				reviewRepository.save(SECOND_REVIEW);
				reviewRepository.save(THIRD_REVIEW);

				//when
				Pageable pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
				Page<Review> reviews = reviewCustomRepository.findAllByOrder(pageRequest);

				//then
				assertThat(reviews.getContent()).hasSize(3);
				assertThat(reviews.getTotalPages()).isEqualTo(1);
			}
		}
	}

	@Nested
	@DisplayName("findTop10ByOrderByCreatedAtDesc 메서드는")
	class Describe_findTop10ByOrderByCreatedAtDesc {
		@Nested
		@DisplayName("최신 리뷰 10개를 조회하면")
		class Context_when_find_top_10 {
			@Test
			@DisplayName("최신 리뷰 10개를 반환한다.")
			void it_returns_top_10_reviews() {
				//given
				perfumeRepository.save(EXISTS_PERFUME);
				userRepository.save(EXISTS_USER);
				reviewRepository.save(EXISTS_REVIEW);
				reviewRepository.save(SECOND_REVIEW);
				reviewRepository.save(THIRD_REVIEW);

				//when
				List<Review> reviews = reviewCustomRepository.findTop10ByOrderByCreatedAtDesc();

				//then
				assertThat(reviews).hasSize(3);
			}
		}
	}
}
