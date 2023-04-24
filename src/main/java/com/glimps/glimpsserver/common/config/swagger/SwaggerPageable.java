package com.glimps.glimpsserver.common.config.swagger;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@ApiModel
@Getter
public class SwaggerPageable {

	@ApiModelProperty(value = "페이지 번호(페이징)")
	private Integer page;

	@ApiModelProperty(value = "페이지 크기", allowableValues = "range[0, 100]")
	private Integer size;

	@ApiModelProperty(value = "시작점(무한 스크롤)")
	private Integer offset;

	@ApiModelProperty(value = "정렬(미구현)")
	private List<String> sort;

}
