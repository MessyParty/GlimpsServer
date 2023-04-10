package com.glimps.glimpsserver.review.dto;

import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel("리뷰 생성 요청")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class ReviewCreateRequest{
	@ApiModelProperty(value = "리뷰 제목 (15자 이하)", required = true, example = "이 향수 좋아요")
	@NotEmpty
	@Size(max = 15)
	private String title;

	@ApiModelProperty(value = "리뷰 내용 (100자 이하)", required = true, example = "이 향수는 너무 좋아요")
	@NotEmpty
	@Size(max = 100)
	private String body;

	@ApiModelProperty(value = "향수에 대한 총평점 (1~5)", required = true, example = "5")
	@Positive
	@Min(value = 0)
	@Max(value = 5)
	private double overallRatings;

	@ApiModelProperty(value = "향수에 대한 잔향점수 (0~5)", required = true, example = "5")
	@Positive
	@Min(value = 0)
	@Max(value = 5)
	private double longevityRatings;

	@ApiModelProperty(value = "향수에 대한 향수점수 (0~5)", required = true, example = "5")
	@Positive
	@Min(value = 0)
	@Max(value = 5)
	private double sillageRatings;

	@ApiModelProperty(value = "향수 UUID", required = true, example = "5")
	@NotNull
	private UUID perfumeUuid;
}
