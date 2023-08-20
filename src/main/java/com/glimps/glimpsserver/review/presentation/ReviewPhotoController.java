package com.glimps.glimpsserver.review.presentation;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.glimps.glimpsserver.review.application.ReviewPhotoService;
import com.glimps.glimpsserver.review.application.ReviewService;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewPhoto;
import com.glimps.glimpsserver.review.dto.ReviewPhotoResponse;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/${api.prefix}/reviews/photos")
public class ReviewPhotoController {
	private final ReviewService reviewService;
	private final ReviewPhotoService reviewPhotoService;

	@PostMapping("/{reviewUuid}")
	@PreAuthorize("isAuthenticated() and hasAuthority('ROLE_USER')")
	@ApiOperation(value = "리뷰 사진 추가", notes = "리뷰 사진을 추가합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "reviewUuid", value = "리뷰 uuid", required = true, dataType = "uuid", paramType = "path"),
		@ApiImplicitParam(name = "files", value = "리뷰 사진", required = true, dataType = "file", paramType = "form")
})
	public List<ReviewPhotoResponse> addPhoto(@PathVariable UUID reviewUuid,
		@RequestPart List<MultipartFile> files) throws
		IOException {
		Review review = reviewService.getReviewById(reviewUuid);
		List<ReviewPhoto> reviewPhotoList = reviewPhotoService.addPhoto(review, files);
		return ReviewPhotoResponse.of(reviewPhotoList);
	}
}
