package com.glimps.glimpsserver.review.presentation;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.glimps.glimpsserver.common.config.SecurityConfig;
import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.config.WithMockCustomUser;
import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.review.application.ReviewService;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.dto.ReviewCreateRequest;
import com.glimps.glimpsserver.review.dto.ReviewPageParam;
import com.glimps.glimpsserver.review.dto.ReviewUpdateRequest;
import com.glimps.glimpsserver.session.application.AuthenticationService;
import com.glimps.glimpsserver.user.domain.RoleType;
import com.glimps.glimpsserver.user.domain.User;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(controllers = ReviewController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class})
	})
@TestPropertySource(properties = {
	"spring.config.location=classpath:application.yml"
})
class ReviewControllerTest {
	private static final String TITLE = "제목입니다.";
	private static final String BODY = "본문입니다.";
	private static final String EXISTS_EMAIL = "exists@email.com";
	private static final String NOT_EXISTS_EMAIL = "noteixsts@email.com";
	private static final UUID EXISTS_REVIEW_UUID = Generators.timeBasedGenerator().generate();
	private static final UUID EXISTS_PERFUME_UUID = Generators.timeBasedGenerator().generate();
	private static final UUID NOT_EXISTS_REVIEW_UUID = Generators.timeBasedGenerator().generate();
	private static final UUID NOT_EXISTS_PERFUME_UUID = UUID.randomUUID();
	private static final Pageable PAGEABLE = PageRequest.of(0, 2, Sort.Direction.DESC, "createdAt");
	private static final Perfume EXISTS_PERFUME = Perfume.builder()
		.uuid(Generators.timeBasedGenerator().generate())
		.perfumeName("향수 이름")
		.brand(Brand.builder().brandNameKor("향수 브랜드").build())
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
	private MockMvc mvc;

	@MockBean
	private ReviewService reviewService;

	@MockBean
	private AuthenticationService authenticationService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${api.prefix}")
	private String contextPath;

	@BeforeEach
	void setUp() {
		assertThat(contextPath).isNotBlank();
		((MockServletContext)mvc.getDispatcherServlet().getServletContext()).setContextPath(contextPath);
	}

	// GET 요청 테스트

	// 인증이 필요없는 로직
	@Nested
	@WithMockUser
	@DisplayName("GET /api/v1/reviews")
	class Describe_list {
		@Nested
		@DisplayName("리뷰가 존재한다면")
		class Context_when_review_exists {
			@BeforeEach
			void setUp() {
				List<Review> reviews = List.of(EXISTS_REVIEW, SECOND_REVIEW, THIRD_REVIEW);
				Page<Review> results = new PageImpl<>(reviews, PAGEABLE, 3);
				given(reviewService.getReviews(any())).willReturn(results);
			}

			@Test
			@DisplayName("상태코드 200과 리뷰 목록을 페이지로 나누어 응답한다.")
			void It_responds_200_and_reviews_by_pagination() throws Exception {
				MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
				map.add("offset", "1");
				map.add("limit", "10");

				mvc.perform(get(contextPath + "/reviews/")
						.params(map)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString(TITLE)))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("리뷰가 존재하지 않는다면")
		class Context_when_review_not_exists {
			@BeforeEach
			void setUp() {
				List<Review> reviews = List.of();
				Page<Review> results = new PageImpl<>(reviews, PAGEABLE, 0);
				given(reviewService.getReviews(any())).willReturn(results);
			}

			@Test
			@DisplayName("상태코드 200과 빈 목록을 페이지로 나누어 응답한다.")
			void It_responds_200_and_empty_list_by_pagination() throws Exception {
				MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
				map.add("offset", "1");
				map.add("limit", "10");

				mvc.perform(get(contextPath + "/reviews/")
						.params(map)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString("[]")))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}
	}

	@Nested
	@WithMockUser
	@DisplayName("GET /api/v1/reviews/{reviewId}")
	class Describe_detail {
		@Nested
		@DisplayName("리뷰가 존재한다면")
		class Context_when_review_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.getReviewById(EXISTS_REVIEW_UUID)).willReturn(EXISTS_REVIEW);
			}

			@Test
			@DisplayName("상태코드 200과 리뷰를 응답한다.")
			void It_responds_200_and_review() throws Exception {
				mvc.perform(get(contextPath + "/reviews/" + EXISTS_REVIEW_UUID)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString(BODY)))
					.andExpect(content().string(containsString(TITLE)))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("리뷰가 존재하지 않는다면")
		class Context_when_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.getReviewById(any())).willThrow(new EntityNotFoundException(
					ErrorCode.REVIEW_NOT_FOUND, NOT_EXISTS_REVIEW_UUID));
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(get(contextPath + "/reviews/" + NOT_EXISTS_REVIEW_UUID))
					.andExpect(status().isNotFound())
					.andDo(print());
			}
		}
	}

	@Nested
	@WithMockUser
	@DisplayName("GET /api/v1/reviews/recentReviews")
	class Describe_recentReviews {
		@Nested
		@DisplayName("최근 리뷰가 존재한다면")
		class Context_when_recent_review_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.getRecentReviews()).willReturn(List.of(EXISTS_REVIEW, SECOND_REVIEW, THIRD_REVIEW));
			}

			@Test
			@DisplayName("상태코드 200과 최근 리뷰를 응답한다")
			void It_responds_200_and_recent_reviews_by_pagination() throws Exception {
				mvc.perform(get(contextPath + "/reviews/recentReviews")
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString(TITLE)))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("최근 리뷰가 존재하지 않는다면")
		class Context_when_recent_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.getRecentReviews()).willReturn(List.of());
			}

			@Test
			@DisplayName("상태코드 200과 빈 목록을 응답한다.")
			void It_responds_200_and_empty_list_by_pagination() throws Exception {
				mvc.perform(get(contextPath + "/reviews/recentReviews")
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString("[]")))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}
	}

	@Nested
	@WithMockUser
	@DisplayName("GET /api/v1/reviews/reviews/perfumeReviews?perfumeId={perfumeUuid}")
	class Describe_perfumeReviews {
		@Nested
		@DisplayName("향수가 존재하고 리뷰가 존재한다면")
		class Context_when_perfume_exists_and_review_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.getPerfumeReviews(EXISTS_PERFUME_UUID)).willReturn(
					List.of(EXISTS_REVIEW, SECOND_REVIEW, THIRD_REVIEW));
			}

			@Test
			@DisplayName("상태코드 200과 향수 리뷰를 응답한다.")
			void It_responds_200_and_perfume_reviews() throws Exception {
				mvc.perform(get(contextPath + "/reviews/perfumeReviews")
						.param("perfumeUuid", EXISTS_PERFUME_UUID.toString())
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString(TITLE)))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("향수가 존재하고 리뷰가 존재하지 않는다면")
		class Context_when_perfume_exists_and_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.getPerfumeReviews(EXISTS_PERFUME_UUID)).willReturn(List.of());
			}

			@Test
			@DisplayName("상태코드 200과 빈 목록을 응답한다.")
			void It_responds_200_and_empty_list() throws Exception {
				mvc.perform(get(contextPath + "/reviews/perfumeReviews")
						.param("perfumeUuid", EXISTS_PERFUME_UUID.toString())
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString("[]")))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("향수가 존재하지 않는다면")
		class Context_when_perfume_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.getPerfumeReviews(NOT_EXISTS_PERFUME_UUID)).willThrow(new EntityNotFoundException(
					ErrorCode.PERFUME_NOT_FOUND, NOT_EXISTS_PERFUME_UUID));
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(get(contextPath + "/reviews/perfumeReviews")
						.param("perfumeUuid", NOT_EXISTS_PERFUME_UUID.toString())
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isNotFound())
					.andDo(print());
			}
		}
	}

	@Nested
	@WithMockUser
	@DisplayName("GET /api/v1/reviews/reviews/bestReviews?amountOfBestReview={amountOfBestReview}")
	class Describe_bestReviews {
		@Nested
		@DisplayName("리뷰가 존재한다면")
		class Context_when_review_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.getBestReviews(3)).willReturn(List.of(SECOND_REVIEW, THIRD_REVIEW, EXISTS_REVIEW));
			}

			@Test
			@DisplayName("상태코드 200과 베스트 리뷰를 추천 순으로 응답한다.")
			void It_responds_200_and_best_reviews_by_heart_cnt() throws Exception {
				mvc.perform(get(contextPath + "/reviews/bestReviews")
						.param("amountOfBestReview", "3")
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString("10")))
					.andExpect(content().string(containsString("7")))
					.andExpect(content().string(containsString("5")))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("리뷰가 존재하지 않는다면")
		class Context_when_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.getBestReviews(3)).willReturn(List.of());
			}

			@Test
			@DisplayName("상태코드 200과 빈 목록을 응답한다.")
			void It_responds_200_and_empty_list() throws Exception {
				mvc.perform(get(contextPath + "/reviews/bestReviews")
						.param("amountOfBestReview", "3")
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString("[]")))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}
	}

	// 인증이 필요한 로직
	@Nested
	@DisplayName("GET /api/v1/reviews/myReviews?offset={offset}&limit={limit}&orderStandard={orderStandard}&sortType={sortType}")
	class Describe_myReviews {
		@Nested
		@WithMockCustomUser
		@DisplayName("사용자가 존재하고 리뷰가 존재한다면")
		class Context_when_user_exists_and_review_exists {
			@BeforeEach
			void setUp() {
				List<Review> reviews = List.of(EXISTS_REVIEW, SECOND_REVIEW, THIRD_REVIEW);
				Page<Review> results = new PageImpl<>(reviews, PAGEABLE, 3);
				given(reviewService.getMyReviews(any(ReviewPageParam.class), any())).willReturn(results);
			}

			@Test
			@DisplayName("상태코드 200과 리뷰를 페이지로 나누어 반환한다.")
			void It_responds_200_and_reviews_by_pagination() throws Exception {
				mvc.perform(get(contextPath + "/reviews/myReviews")
						.param("offset", "0")
						.param("limit", "3")
						.param("orderStandard", "DATE")
						.param("sortType", "DESC")
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString("10")))
					.andExpect(content().string(containsString("7")))
					.andExpect(content().string(containsString("5")))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}

		@Nested
		@WithMockCustomUser
		@DisplayName("사용자가 존재하고 리뷰가 존재하지 않는다면")
		class Context_when_user_exists_and_review_not_exists {
			@BeforeEach
			void setUp() {
				List<Review> reviews = List.of();
				Page<Review> results = new PageImpl<>(reviews, PAGEABLE, 0);
				given(reviewService.getMyReviews(any(ReviewPageParam.class), any())).willReturn(results);
			}

			@Test
			@DisplayName("상태코드 200과 빈 목록을 응답한다.")
			void It_responds_200_and_empty_list() throws Exception {
				mvc.perform(get(contextPath + "/reviews/myReviews")
						.param("offset", "0")
						.param("limit", "3")
						.param("orderStandard", "DATE")
						.param("sortType", "DESC")
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(content().string(containsString("[]")))
					.andExpect(status().isOk())
					.andDo(print());
			}
		}

		@Nested
		@WithMockCustomUser(userName = NOT_EXISTS_EMAIL)
		@DisplayName("사용자가 존재하지 않을 때")
		class Context_when_user_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.getMyReviews(any(ReviewPageParam.class), eq(NOT_EXISTS_EMAIL)))
					.willThrow(new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, NOT_EXISTS_EMAIL));
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(get(contextPath + "/reviews/myReviews")
						.param("offset", "0")
						.param("limit", "3")
						.param("orderStandard", "DATE")
						.param("sortType", "DESC")
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isNotFound())
					.andDo(print());
			}
		}

		@Nested
		@WithMockCustomUser
		@DisplayName("올바른 파라미터가 아니라면")
		class Context_when_invalid_parameter {
			@Test
			@DisplayName("상태코드 400을 응답한다.")
			void It_responds_400() throws Exception {
				mvc.perform(get(contextPath + "/reviews/myReviews")
						.param("offset", "0")
						.param("orderStandard", "DATE")
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isBadRequest())
					.andDo(print());
			}
		}
	}

	@Nested
	@WithMockCustomUser
	@DisplayName("POST /api/v1/{uuid}/reviews")
	class Describe_createHeart {
		@Nested
		@DisplayName("사용자가 존재하고 리뷰가 존재할 때")
		class Context_when_user_exists_and_review_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.createHeart(eq(EXISTS_REVIEW_UUID), any()))
					.willReturn(EXISTS_REVIEW);
			}

			@Test
			@DisplayName("좋아요를 생성하고 상태코드 201과 리뷰를 응답한다.")
			void It_responds_201_and_review() throws Exception {
				mvc.perform(post(contextPath + "/reviews/" + EXISTS_REVIEW_UUID + "/heart")
						.with(csrf())
					)
					.andExpect(status().isCreated())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("사용자가 존재하고 리뷰가 존재하지 않을 때")
		class Context_when_user_exists_and_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.createHeart(eq(NOT_EXISTS_REVIEW_UUID), any())).willThrow(
					new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND, EXISTS_REVIEW_UUID)
				);
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(post(contextPath + "/reviews/" + NOT_EXISTS_REVIEW_UUID + "/heart")
						.with(csrf())
					)
					.andExpect(status().isNotFound());
			}
		}

		@Nested
		@WithMockCustomUser(userName = NOT_EXISTS_EMAIL)
		@DisplayName("사용자가 존재하지 않을 때")
		class Context_when_user_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.createHeart(any(), eq(NOT_EXISTS_EMAIL)))
					.willThrow(new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, NOT_EXISTS_EMAIL));
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(post(contextPath + "/reviews/" + EXISTS_REVIEW_UUID + "/heart")
						.with(csrf())
					)
					.andExpect(status().isNotFound());
			}
		}
	}

	@Nested
	@WithMockCustomUser
	@DisplayName("DELETE /api/v1/reviews/{uuid}/heart")
	class Describe_cancelHeart {
		@Nested
		@DisplayName("사용자가 존재하고 리뷰가 존재할 때")
		class Context_when_user_exists_and_review_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.cancelHeart(eq(EXISTS_REVIEW_UUID), any())).willReturn(EXISTS_REVIEW);
			}

			@Test
			@DisplayName("좋아요를 취소하고 상태코드 204를 응답한다.")
			void It_responds_204() throws Exception {
				mvc.perform(delete(contextPath + "/reviews/" + EXISTS_REVIEW_UUID + "/heart")
						.with(csrf())
					)
					.andExpect(status().isNoContent())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("사용자가 존재하고 리뷰가 존재하지 않을 때")
		class Context_when_user_exists_and_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.cancelHeart(eq(NOT_EXISTS_REVIEW_UUID), any())).willThrow(
					new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND, EXISTS_REVIEW_UUID)
				);
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(delete(contextPath + "/reviews/"  + NOT_EXISTS_REVIEW_UUID + "/heart")
						.with(csrf())
					)
					.andExpect(status().isNotFound());
			}
		}

		@Nested
		@WithMockCustomUser(userName = NOT_EXISTS_EMAIL)
		@DisplayName("사용자가 존재하지 않을 때")
		class Context_when_user_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.cancelHeart(any(), eq(NOT_EXISTS_EMAIL)))
					.willThrow(new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, NOT_EXISTS_EMAIL));
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(delete(contextPath + "/reviews/" + EXISTS_REVIEW_UUID + "/heart")
						.with(csrf())
					)
					.andExpect(status().isNotFound());
			}
		}
	}

	@Nested
	@WithMockCustomUser
	@DisplayName("POST /api/v1/reviews")
	class Describe_create {
		private final ReviewCreateRequest validCreateRequest = ReviewCreateRequest.builder()
			.title(TITLE)
			.body(BODY)
			.overallRatings(5)
			.longevityRatings(3)
			.sillageRatings(3)
			.perfumeUuid(EXISTS_PERFUME_UUID)
			.build();

		@Nested
		@DisplayName("사용자가 존재하고 올바른 요청일 때")
		class Context_when_user_exists_and_review_exists_with_valid_request {
			@BeforeEach
			void setUp() {
				given(reviewService.createReview(any(), any())).willReturn(EXISTS_REVIEW);
			}

			@Test
			@DisplayName("리뷰를 생성하고 상태코드 201과 리뷰를 응답한다.")
			void It_responds_201_and_review() throws Exception {
				mvc.perform(post(contextPath + "/reviews/")
						.with(csrf())
						.content(objectMapper.writeValueAsString(validCreateRequest))
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isCreated())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("사용자가 존재하고 올바르지 않은 제목이 있을 때")
		class Context_when_user_exists_and_review_exists_with_invalid_title {
			private final ReviewCreateRequest invalidCreateRequest = ReviewCreateRequest.builder()
				.title(TITLE + "12312312312312312")
				.body(BODY)
				.overallRatings(5)
				.longevityRatings(3)
				.sillageRatings(3)
				.perfumeUuid(EXISTS_PERFUME_UUID)
				.build();

			@Test
			@DisplayName("상태코드 400을 응답한다.")
			void It_responds_400() throws Exception {
				mvc.perform(post(contextPath + "/reviews/")
						.with(csrf())
						.content(objectMapper.writeValueAsString(invalidCreateRequest))
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isBadRequest())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("사용자가 존재하고 올바르지 않은 요청일 때")
		class Context_when_user_exists_and_review_exists_and_invalid_point {
			private final ReviewCreateRequest invalidCreateRequest = ReviewCreateRequest.builder()
				.title(TITLE)
				.body(BODY)
				.overallRatings(6)
				.longevityRatings(3)
				.sillageRatings(3)
				.perfumeUuid(EXISTS_PERFUME_UUID)
				.build();

			@Test
			@DisplayName("상태코드 400을 응답한다.")
			void It_responds_400() throws Exception {
				mvc.perform(post(contextPath + "/reviews/")
						.with(csrf())
						.content(objectMapper.writeValueAsString(invalidCreateRequest))
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isBadRequest())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("사용자가 존재하지 않을 때")
		class Context_when_user_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.createReview(any(), any())).willThrow(
					new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, NOT_EXISTS_EMAIL)
				);
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(post(contextPath + "/reviews/")
						.with(csrf())
						.content(objectMapper.writeValueAsString(validCreateRequest))
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isNotFound())
					.andDo(print());
			}
		}
	}

	@Nested
	@WithMockCustomUser
	@DisplayName("PATCH /api/v1/reviews/{reviewId}")
	class Describe_update {
		@Nested
		@DisplayName("사용자가 존재하고 리뷰가 존재하고 올바른 요청일 때")
		class Context_when_user_exists_and_review_exists_and_valid_request {
			private final ReviewUpdateRequest validUpdateRequest = ReviewUpdateRequest.builder()
				.title(TITLE)
				.body(BODY)
				.overallRatings(5.0)
				.longevityRatings(3.0)
				.sillageRatings(3.0)
				.build();

			@BeforeEach
			void setUp() {
				given(reviewService.updateReview(any(), any(ReviewUpdateRequest.class), any())).willReturn(
					EXISTS_REVIEW);
			}

			@Test
			@DisplayName("리뷰를 수정하고 상태코드 200과 리뷰를 응답한다.")
			void It_responds_200_and_review() throws Exception {
				mvc.perform(patch(contextPath + "/reviews/" + EXISTS_REVIEW_UUID)
						.with(csrf())
						.content(objectMapper.writeValueAsString(validUpdateRequest))
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isOk())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("사용자가 존재하고 리뷰가 존재하고 올바르지 않은 요청일 때")
		class Context_when_user_exists_and_review_exists_and_invalid_request {
			private final ReviewUpdateRequest invalidUpdateRequest = ReviewUpdateRequest.builder()
				.title(TITLE)
				.body(BODY)
				.overallRatings(-5.0)
				.longevityRatings(3.0)
				.sillageRatings(3.0)
				.build();

			@Test
			@DisplayName("상태코드 400을 응답한다.")
			void It_responds_400() throws Exception {
				mvc.perform(patch(contextPath + "/reviews/" + EXISTS_REVIEW_UUID)
						.with(csrf())
						.content(objectMapper.writeValueAsString(invalidUpdateRequest))
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isBadRequest())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("사용자가 존재하고 리뷰가 존재하지 않을 때")
		class Context_when_user_exists_and_review_not_exists {
			private final ReviewUpdateRequest validUpdateRequest = ReviewUpdateRequest.builder()
				.title(TITLE)
				.body(BODY)
				.overallRatings(5.0)
				.longevityRatings(3.0)
				.sillageRatings(3.0)
				.build();

			@BeforeEach
			void setUp() {
				given(reviewService.updateReview(any(), any(ReviewUpdateRequest.class), any())).willThrow(
					new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND, NOT_EXISTS_REVIEW_UUID)
				);
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(patch(contextPath + "/reviews/" + NOT_EXISTS_REVIEW_UUID)
						.with(csrf())
						.content(objectMapper.writeValueAsString(validUpdateRequest))
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isNotFound())
					.andDo(print());
			}
		}

		@Nested
		@WithMockCustomUser(userName = NOT_EXISTS_EMAIL)
		@DisplayName("사용자가 존재하지 않을 때")
		class Context_when_user_not_exists {
			private final ReviewUpdateRequest validUpdateRequest = ReviewUpdateRequest.builder()
				.title(TITLE)
				.body(BODY)
				.overallRatings(5.0)
				.longevityRatings(3.0)
				.sillageRatings(3.0)
				.build();

			@BeforeEach
			void setUp() {
				given(
					reviewService.updateReview(any(), any(ReviewUpdateRequest.class), eq(NOT_EXISTS_EMAIL))).willThrow(
					new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, NOT_EXISTS_EMAIL)
				);
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(patch(contextPath + "/reviews/" + EXISTS_REVIEW_UUID)
						.with(csrf())
						.content(objectMapper.writeValueAsString(validUpdateRequest))
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isNotFound())
					.andDo(print());
			}
		}
	}

	@Nested
	@WithMockCustomUser
	@DisplayName("DELETE /api/v1/reviews/{reviewId}")
	class Describe_delete {
		@Nested
		@DisplayName("사용자가 존재하고 리뷰가 존재할 때")
		class Context_when_user_exists_and_review_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.deleteReview(any(), any())).willReturn(EXISTS_REVIEW);
			}

			@Test
			@DisplayName("리뷰를 삭제하고 상태코드 204를 응답한다.")
			void It_responds_204() throws Exception {
				mvc.perform(delete(contextPath + "/reviews/" + EXISTS_REVIEW_UUID)
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isNoContent())
					.andDo(print());
			}
		}

		@Nested
		@DisplayName("사용자가 존재하고 리뷰가 존재하지 않을 때")
		class Context_when_user_exists_and_review_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.deleteReview(any(), any())).willThrow(
					new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND, NOT_EXISTS_REVIEW_UUID)
				);
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(delete(contextPath + "/reviews/" + NOT_EXISTS_REVIEW_UUID)
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isNotFound())
					.andDo(print());
			}
		}

		@Nested
		@WithMockCustomUser(userName = NOT_EXISTS_EMAIL)
		@DisplayName("사용자가 존재하지 않을 때")
		class Context_when_user_not_exists {
			@BeforeEach
			void setUp() {
				given(reviewService.deleteReview(any(), eq(NOT_EXISTS_EMAIL))).willThrow(
					new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, NOT_EXISTS_EMAIL)
				);
			}

			@Test
			@DisplayName("상태코드 404를 응답한다.")
			void It_responds_404() throws Exception {
				mvc.perform(delete(contextPath + "/reviews/" + EXISTS_REVIEW_UUID)
						.with(csrf())
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
					)
					.andExpect(status().isNotFound())
					.andDo(print());
			}
		}
	}
}
