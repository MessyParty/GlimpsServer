package com.glimps.glimpsserver.perfume.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Setter;

@Setter
public class PerfumeSearchCondition {

	private String brand;

	private String perfume;

	private String note;

	@ApiModelProperty(name = "brand", value = "검색할 브랜드")
	public String getBrandName() {
		return this.brand;
	}

	@ApiModelProperty(name = "perfume", value = "검색할 향수 이름", example = "CK")
	public String getPerfumeName() {
		return this.perfume;
	}

	@ApiModelProperty(name = "note", value = "검색할 노트의 이름")
	public String getNoteName() {
		return this.note;
	}
}
