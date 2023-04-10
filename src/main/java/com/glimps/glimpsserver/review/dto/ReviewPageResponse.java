package com.glimps.glimpsserver.review.dto;

import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewPhoto;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class ReviewPageResponse {
	private String title;
	private String body;
	private UUID uuid;
	private String nickname;
	private List<String> photoUrl;
	private String perfumeName;
	private String perfumeBrandKor;
	private String perfumeBrandEng;
	private double overallRating;
	private double longevityRating;
	private double sillageRating;
	private int heartsCnt;
	private long totalElements;
	private long totalPages;
	private boolean hasNext;

	public static List<ReviewPageResponse> of(Page<Review> reviews) {
		if (reviews.getContent().isEmpty()) {
			return List.of();
		}
		return reviews.getContent().stream()
			.map(getFunction(reviews.getTotalElements(), reviews.getTotalPages(), reviews.hasNext()))
			.collect(Collectors.toList());
	}

	private static Function<Review, ReviewPageResponse> getFunction(long totalElements, long totalPages,
		boolean hasNext) {
		return review -> ReviewPageResponse.builder()
			.title(review.getTitle())
			.body(review.getBody())
			.uuid(review.getUuid())
			.nickname(review.getUser().getNickname())
			.photoUrl(review.getReviewPhotos().stream()
				.map(ReviewPhoto::getUrl)
				.collect(Collectors.toList()))
			.perfumeName(review.getPerfume().getPerfumeName())
			// TODO N+1 문제 발생 가능, FETCH JOIN 필요
			.perfumeBrandKor(review.getPerfume().getBrand().getBrandNameKor())
			.perfumeBrandEng(review.getPerfume().getBrand().getBrandNameEng())
			.overallRating(review.getOverallRatings())
			.longevityRating(review.getLongevityRatings())
			.sillageRating(review.getSillageRatings())
			.heartsCnt(review.getHeartsCnt())
			.totalElements(totalElements)
			.totalPages(totalPages)
			.hasNext(hasNext)
			.build();

	}
}
