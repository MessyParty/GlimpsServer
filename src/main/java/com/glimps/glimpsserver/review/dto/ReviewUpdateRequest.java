package com.glimps.glimpsserver.review.dto;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ApiModel("리뷰 수정 요청")
public class ReviewUpdateRequest {
	@ApiModelProperty(value = "리뷰 제목 (15자 이하)", required = true, example = "이 향수 좋아요")
	@NotEmpty
	@Size(max = 15)
	private String title;

	@ApiModelProperty(value = "리뷰 내용 (100자 이하)", required = true, example = "이 향수는 너무 좋아요")
	@NotEmpty
	private String body;

	@ApiModelProperty(value = "향수에 대한 총평점 (0~5)", example = "5")
	@Positive
	@Min(value = 0)
	@Max(value = 5)
	private Double overallRatings;

	@ApiModelProperty(value = "향수에 대한 잔향점수 (0~5)", example = "5")
	@Positive
	@Min(value = 0)
	@Max(value = 5)
	private Double longevityRatings;

	@ApiModelProperty(value = "향수에 대한 향수점수 (0~5)", example = "5")
	@Positive
	@Min(value = 0)
	@Max(value = 5)
	private Double sillageRatings;

	@ApiModelProperty(value = "사진 Url", example = "[\"https://s3.ap-northeast-2.amazonaws.com/glimps/5e7f1b0c-7f1b-4b0c-8f1b-0c7f1b4b0c7f.jpg\"]")
	@Builder.Default
	@NotNull
	private List<String> photoUrls = Lists.newArrayList();
}
