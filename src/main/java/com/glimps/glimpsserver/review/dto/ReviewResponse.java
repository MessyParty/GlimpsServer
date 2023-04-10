package com.glimps.glimpsserver.review.dto;

import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewPhoto;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class ReviewResponse {
	private String title;
	private String body;
	private String nickname;
	private UUID uuid;
	@Builder.Default
	private List<String> photoUrls = Lists.newArrayList();
	private String perfumeName;
	private String perfumeBrand;
	private int heartCnt;
	private double overallRatings;
	private double longevityRatings;
	private double sillageRatings;

	private LocalDateTime createdAt;

	public static ReviewResponse of(Review review) {
		return ReviewResponse.builder()
			.title(review.getTitle())
			.body(review.getBody())
			.uuid(review.getUuid())
			.nickname(review.getUser().getNickname())
			.photoUrls(review.getReviewPhotos().stream()
				.map(ReviewPhoto::getUrl)
				.collect(Collectors.toList()))
			.perfumeName(review.getPerfume().getPerfumeName())
			.heartCnt(review.getHeartsCnt())
			.perfumeBrand(review.getPerfume().getBrand().getBrandName())
			.overallRatings(review.getOverallRatings())
			.longevityRatings(review.getLongevityRatings())
			.sillageRatings(review.getSillageRatings())
			.createdAt(review.getCreatedAt())
			.build();
	}

	public static List<ReviewResponse> of(List<Review> reviews) {
		return reviews.stream()
			.map(ReviewResponse::of)
			.collect(Collectors.toList());
	}
}
