package com.glimps.glimpsserver.review.dto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.glimps.glimpsserver.common.domain.CustomPage;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewPhoto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class ReviewPageResponse {
	private String title;
	private String body;
	private String nickname;
	private List<String> photoUrl;
	private String perfumeName;
	private String perfumeBrand;
	private double overallRating;
	private double longevityRating;
	private double sillageRating;
	private long totalElements;
	private long totalPages;

	public static List<ReviewPageResponse> of(CustomPage<Review> reviews) {
		return reviews.getContent().stream()
			.map(getFunction(reviews.getTotalElements(), reviews.getTotalPages()))
			.collect(Collectors.toList());
	}

	private static Function<Review, ReviewPageResponse> getFunction(long totalElements, long totalPages) {
		return review -> ReviewPageResponse.builder()
			.title(review.getTitle())
			.body(review.getBody())
			.nickname(review.getUser().getNickname())
			.photoUrl(review.getReviewPhotos().stream()
				.map(ReviewPhoto::getUrl)
				.collect(Collectors.toList()))
			.perfumeName(review.getPerfume().getPerfumeName())
			.perfumeBrand(review.getPerfume().getBrand())
			.overallRating(review.getOverallRatings())
			.longevityRating(review.getLongevityRatings())
			.sillageRating(review.getSillageRatings())
			.totalElements(totalElements)
			.totalPages(totalPages)
			.build();

	}
}
