package com.glimps.glimpsserver.review.presentation;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.common.authentication.UserAuthentication;
import com.glimps.glimpsserver.review.application.ReviewService;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.dto.ReviewCreateRequest;
import com.glimps.glimpsserver.review.dto.ReviewPageParam;
import com.glimps.glimpsserver.review.dto.ReviewPageResponse;
import com.glimps.glimpsserver.review.dto.ReviewResponse;
import com.glimps.glimpsserver.review.dto.ReviewUpdateRequest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	@ApiOperation(value = "리뷰 생성", notes = "리뷰를 생성합니다. (권한: ROLE_USER)")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("isAuthenticated() and hasAuthority('ROLE_USER')")
	public ReviewResponse create(@RequestBody @Valid ReviewCreateRequest reviewCreateRequest,
			UserAuthentication userAuthentication) {
		String email = userAuthentication.getEmail();
		Review review = reviewService.createReview(reviewCreateRequest, email);
		return ReviewResponse.of(review);
	}

	@ApiOperation(value = "리뷰 조회", notes = "리뷰를 uuid로 조회합니다.")
	@ApiImplicitParam(name = "uuid", value = "리뷰 uuid", required = true, dataType = "string", paramType = "path")
	@GetMapping("/{uuid}")
	public ReviewResponse detail(@PathVariable UUID uuid) {
		Review review = reviewService.getReviewById(uuid);
		return ReviewResponse.of(review);
	}

	@ApiOperation(value = "마이 리뷰 조회", notes = "내가 작성한 리뷰를 조회합니다. (권한: ROLE_USER)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "limit", value = "조회할 개수", readOnly = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "offset", value = "조회할 시작 위치", readOnly = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "sortType", value = "정렬 기준(ASC or DESC)", readOnly = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "orderStandard", value = "정렬 기준(DATE or HEARTS_COUNT)", readOnly = true, dataType = "string", paramType = "query")
	})
	@GetMapping("/myReviews")
	@PreAuthorize("isAuthenticated() and hasAuthority('ROLE_USER')")
	public List<ReviewPageResponse> myReviews(ReviewPageParam reviewPageParam, UserAuthentication userAuthentication) {
		String email = userAuthentication.getEmail();
		Page<Review> reviews = reviewService.getMyReviews(reviewPageParam, email);
		return ReviewPageResponse.of(reviews);
	}

	@ApiOperation(value = "최근 리뷰 조회", notes = "최근 리뷰를 조회합니다.")
	@GetMapping("/recentReviews")
	public List<ReviewResponse> recentReviews() {
		List<Review> reviews = reviewService.getRecentReviews();
		return ReviewResponse.of(reviews);
	}

	@ApiOperation(value = "모든 리뷰 조회", notes = "모든 리뷰를 조회합니다.")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "limit", value = "조회할 개수", readOnly = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "offset", value = "조회할 시작 위치", readOnly = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "sortType", value = "정렬 기준(ASC or DESC)", readOnly = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "orderStandard", value = "정렬 기준(DATE or HEARTS_COUNT)", readOnly = true, dataType = "string", paramType = "query")
	})
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ReviewPageResponse> list(ReviewPageParam reviewPageParam) {
		Page<Review> reviews = reviewService.getReviews(reviewPageParam);
		return ReviewPageResponse.of(reviews);
	}

	@ApiOperation(value = "좋아요 생성", notes = "리뷰에 좋아요를 생성합니다. (권한: ROLE_USER)")
	@ApiImplicitParam(name = "uuid", value = "리뷰 uuid", required = true, dataType = "string", paramType = "path")
	@PostMapping("/{uuid}/heart")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("isAuthenticated() and hasAuthority('ROLE_USER')")
	public ReviewResponse createHeart(@PathVariable UUID uuid, UserAuthentication userAuthentication) {
		String email = userAuthentication.getEmail();
		Review review = reviewService.createHeart(uuid, email);
		return ReviewResponse.of(review);
	}

	@ApiOperation(value = "좋아요 취소", notes = "리뷰에 좋아요를 취소합니다. (권한: ROLE_USER)")
	@ApiImplicitParam(name = "uuid", value = "리뷰 uuid", required = true, dataType = "string", paramType = "path")
	@DeleteMapping("/{uuid}/heart")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("isAuthenticated() and hasAuthority('ROLE_USER')")
	public ReviewResponse cancelHeart(@PathVariable UUID uuid, UserAuthentication userAuthentication) {
		String email = userAuthentication.getEmail();
		Review review = reviewService.cancelHeart(uuid, email);
		return ReviewResponse.of(review);
	}

	@ApiOperation(value = "향수 리뷰 조회", notes = "향수에 대한 리뷰를 조회합니다.")
	@ApiImplicitParam(name = "perfumeUuid", value = "향수 uuid", required = true, dataType = "string", paramType = "query")
	@GetMapping("/perfumeReviews")
	public List<ReviewResponse> perfumeReviews(@RequestParam UUID perfumeUuid) {
		List<Review> reviews = reviewService.getPerfumeReviews(perfumeUuid);
		return ReviewResponse.of(reviews);
	}

	@ApiOperation(value = "베스트 리뷰 조회", notes = "베스트 리뷰를 조회합니다.")
	@ApiImplicitParam(name = "amountOfBestReview", value = "베스트 리뷰 개수", required = true, dataType = "int", paramType = "query")
	@GetMapping("/bestReviews")
	public List<ReviewResponse> bestReviews(@RequestParam int amountOfBestReview) {
		List<Review> reviews = reviewService.getBestReviews(amountOfBestReview);
		return ReviewResponse.of(reviews);
	}

	@ApiOperation(value = "리뷰 수정", notes = "리뷰를 수정합니다. (권한: ROLE_USER)")
	@ApiImplicitParam(name = "uuid", value = "리뷰 uuid", required = true, dataType = "string", paramType = "path")
	@PatchMapping("/{uuid}")
	@PreAuthorize("isAuthenticated() and hasAuthority('ROLE_USER')")
	public ReviewResponse update(@PathVariable UUID uuid, @RequestBody @Valid ReviewUpdateRequest reviewUpdateRequest,
			UserAuthentication userAuthentication) {
		String email = userAuthentication.getEmail();
		Review review = reviewService.updateReview(uuid, reviewUpdateRequest, email);
		return ReviewResponse.of(review);
	}

	@ApiOperation(value = "리뷰 삭제", notes = "리뷰를 삭제합니다. (권한: ROLE_USER)")
	@ApiImplicitParam(name = "uuid", value = "리뷰 uuid", required = true, dataType = "string", paramType = "path")
	@DeleteMapping("/{uuid}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("isAuthenticated() and hasAuthority('ROLE_USER')")
	public void delete(@PathVariable UUID uuid, UserAuthentication userAuthentication) {
		String email = userAuthentication.getEmail();
		reviewService.deleteReview(uuid, email);
	}

}
