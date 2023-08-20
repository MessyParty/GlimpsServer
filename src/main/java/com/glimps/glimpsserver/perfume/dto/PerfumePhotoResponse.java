package com.glimps.glimpsserver.perfume.dto;

import com.glimps.glimpsserver.perfume.domain.PerfumePhoto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerfumePhotoResponse {

	private String url;

	private Integer order;
	public static PerfumePhotoResponse of(PerfumePhoto photo) {
		return PerfumePhotoResponse.builder()
			.url(photo.getUrl())
			.order(photo.getOrderNum())
			.build();
	}

}
