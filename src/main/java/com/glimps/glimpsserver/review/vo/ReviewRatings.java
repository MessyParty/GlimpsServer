package com.glimps.glimpsserver.review.vo;

import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewRatings {
	@Positive
	private double overallRatings;
	@Positive
	private double longevityRatings;
	@Positive
	private double sillageRatings;
}
