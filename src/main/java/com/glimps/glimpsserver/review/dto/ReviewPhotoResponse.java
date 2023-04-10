package com.glimps.glimpsserver.review.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.glimps.glimpsserver.review.domain.ReviewPhoto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewPhotoResponse {
	private String photoUrl;

	public static List<ReviewPhotoResponse> of(List<ReviewPhoto> reviewPhotoList) {
		return reviewPhotoList.stream()
			.map(reviewPhoto -> new ReviewPhotoResponse(reviewPhoto.getUrl()))
			.collect(Collectors.toList());
	}
}
