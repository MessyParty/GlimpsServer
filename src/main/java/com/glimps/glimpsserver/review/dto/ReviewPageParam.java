package com.glimps.glimpsserver.review.dto;

import java.util.Objects;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.glimps.glimpsserver.common.domain.ReviewOrderStandard;
import com.glimps.glimpsserver.common.domain.SortType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel("리뷰 페이지 정보")
public class ReviewPageParam {
	@ApiModelProperty(value = "가져올 개수", required = true, example = "10")
	@NotNull
	private final int limit;

	@ApiModelProperty(value = "정렬 기준", required = true, example = "DATE")
	@Enumerated(value = EnumType.STRING)
	private final ReviewOrderStandard orderStandard;

	@ApiModelProperty(value = "정렬 방식", required = true, example = "DESC")
	@Enumerated(value = EnumType.STRING)
	private final SortType sortType;

	@ApiModelProperty(value = "기준", example = "0")
	private final Integer offset;

	public ReviewPageParam(Integer offset, int limit, ReviewOrderStandard orderStandard, SortType sortType) {
		this.offset = Objects.requireNonNullElse(offset, 0);
		this.orderStandard = Objects.requireNonNullElse(orderStandard, ReviewOrderStandard.DATE);
		this.limit = limit;
		this.sortType = Objects.requireNonNullElse(sortType, SortType.DESC);

	}
}
