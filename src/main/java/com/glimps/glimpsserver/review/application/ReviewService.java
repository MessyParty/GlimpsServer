package com.glimps.glimpsserver.review.application;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.common.error.ReviewAuthorityException;
import com.glimps.glimpsserver.perfume.application.PerfumeService;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.dto.ReviewCreateRequest;
import com.glimps.glimpsserver.review.dto.ReviewPageParam;
import com.glimps.glimpsserver.review.dto.ReviewUpdateRequest;
import com.glimps.glimpsserver.review.infra.ReviewCustomRepository;
import com.glimps.glimpsserver.review.infra.ReviewRepository;
import com.glimps.glimpsserver.review.vo.ReviewRatings;
import com.glimps.glimpsserver.user.application.UserService;
import com.glimps.glimpsserver.user.domain.User;

@Service
@Transactional(readOnly = true)
public class ReviewService {
	private final ReviewCustomRepository reviewCustomRepository;
	private final ReviewRepository reviewRepository;
	private final UserService userService;
	private final ReviewPhotoService reviewPhotoService;
	private final PerfumeService perfumeService;
	private final ReviewHeartService reviewHeartService;

	public ReviewService(ReviewCustomRepository reviewCustomRepository, ReviewRepository reviewRepository,
		UserService userService,
		ReviewPhotoService reviewPhotoService, PerfumeService perfumeService, ReviewHeartService reviewHeartService) {
		this.reviewCustomRepository = reviewCustomRepository;
		this.reviewRepository = reviewRepository;
		this.userService = userService;
		this.reviewPhotoService = reviewPhotoService;
		this.perfumeService = perfumeService;
		this.reviewHeartService = reviewHeartService;
	}

	@Transactional
	public Review createReview(ReviewCreateRequest reviewCreateRequest, String email) {
		User user = userService.getUserByEmail(email);
		UUID perfumeUuid = reviewCreateRequest.getPerfumeUuid();
		Perfume perfume = perfumeService.getPerfumeById(perfumeUuid);

		Review review = Review.createReview(reviewCreateRequest, user, perfume);

		perfumeService.updateRatings(perfume, reviewCreateRequest);

		return reviewRepository.save(review);
	}

	public Review getReviewById(UUID uuid) {
		return findReview(uuid);
	}

	public Page<Review> getMyReviews(ReviewPageParam reviewPageParam, String email) {
		int offset = reviewPageParam.getOffset();

		Pageable pageRequest = PageRequest.of(offset, reviewPageParam.getLimit(),
			reviewPageParam.getSortType().getDirection(), reviewPageParam.getOrderStandard().getProperty());

		User user = userService.getUserByEmail(email);

		return reviewCustomRepository.findAllByUserId(user.getId(), pageRequest);
	}

	public List<Review> getRecentReviews() {
		return reviewCustomRepository.findTop10ByOrderByCreatedAtDesc();
	}

	public Page<Review> getReviews(ReviewPageParam reviewPageParam) {
		int offset = reviewPageParam.getOffset();

		Pageable pageRequest = PageRequest.of(offset, reviewPageParam.getLimit(),
			reviewPageParam.getSortType().getDirection(),
			reviewPageParam.getOrderStandard().getProperty());
		return reviewCustomRepository.findAllByOrder(pageRequest);
	}

	@Transactional
	public Review createHeart(UUID reviewId, String existsEmail) {
		Review review = findReview(reviewId);
		User user = userService.getUserByEmail(existsEmail);
		reviewHeartService.createReviewHeart(review, user);

		return review;
	}

	@Transactional
	public Review cancelHeart(UUID uuid, String email) {
		Review review = findReview(uuid);
		User user = userService.getUserByEmail(email);
		reviewHeartService.cancelReviewHeart(review, user);
		return review;
	}

	public List<Review> getPerfumeReviews(UUID perfumeUuid) {
		return reviewCustomRepository.findAllByPerfumeId(perfumeUuid);
	}

	public List<Review> getBestReviews(int amountOfBestReview) {
		return reviewCustomRepository.findBestReviewByAmount(amountOfBestReview);
	}

	private Review findReview(UUID uuid) {
		return reviewCustomRepository.findByUuid(uuid)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND, uuid));
	}

	public Review updateReview(UUID uuid, ReviewUpdateRequest reviewUpdateRequest, String email) {
		Review review = findReview(uuid);
		User user = userService.getUserByEmail(email);
		if (!review.authorize(user)) {
			throw new ReviewAuthorityException(ErrorCode.NO_AUTHORITY, email);
		}

		Perfume perfume = review.getPerfume();

		if (reviewUpdateRequest.getOverallRatings() != null && reviewUpdateRequest.getLongevityRatings() != null
			&& reviewUpdateRequest.getSillageRatings() != null) {
			ReviewRatings reviewRatings = new ReviewRatings(reviewUpdateRequest.getOverallRatings(),
				reviewUpdateRequest.getLongevityRatings(), reviewUpdateRequest.getSillageRatings());
			perfumeService.updateRatings(perfume, reviewUpdateRequest, reviewRatings);
		}

		reviewPhotoService.updateReviewPhotos(review,
			reviewUpdateRequest.getPhotoUrls());
		review.updateReview(reviewUpdateRequest);
		return review;
	}

	public Review deleteReview(UUID uuid, String email) {
		Review review = findReview(uuid);
		User user = userService.getUserByEmail(email);
		if (!review.authorize(user)) {
			throw new ReviewAuthorityException(ErrorCode.NO_AUTHORITY, email);
		}
		ReviewRatings reviewRatings = new ReviewRatings(review.getOverallRatings(), review.getLongevityRatings(),
			review.getSillageRatings());
		perfumeService.updateRatings(review.getPerfume(), reviewRatings);
		reviewPhotoService.deleteReviewPhotos(review);
		reviewRepository.delete(review);
		return review;
	}
}
