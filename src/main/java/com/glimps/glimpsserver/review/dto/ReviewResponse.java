package com.glimps.glimpsserver.review.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewPhoto;
import com.google.common.collect.Lists;

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
	private String perfumeBrandKor;
	private String perfumeBrandEng;
	private int heartCnt;
	private double overallRatings;
	private double longevityRatings;
	private double sillageRatings;
	private double scentRatings;

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
			.perfumeBrandKor(review.getPerfume().getBrand().getBrandNameKor())
			.perfumeBrandEng(review.getPerfume().getBrand().getBrandNameEng())
			.overallRatings(review.getOverallRatings())
			.longevityRatings(review.getLongevityRatings())
			.sillageRatings(review.getSillageRatings())
			.scentRatings(review.getScentRatings())
			.createdAt(review.getCreatedAt())
			.build();
	}

	public static List<ReviewResponse> of(List<Review> reviews) {
		return reviews.stream()
			.map(ReviewResponse::of)
			.collect(Collectors.toList());
	}

	public static Page<ReviewResponse> convert(Page<Review> reviews) {
		if (reviews.getContent().isEmpty()) {
			return Page.empty();
		}
		return reviews.map(ReviewResponse::of);
	}
}
