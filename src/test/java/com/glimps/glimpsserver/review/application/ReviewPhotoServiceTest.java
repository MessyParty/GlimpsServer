package com.glimps.glimpsserver.review.application;

import static org.mockito.BDDMockito.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewPhoto;
import com.glimps.glimpsserver.review.infra.ReviewPhotoRepository;

@SpringBootTest
class ReviewPhotoServiceTest {
	private static final Review VALID_REVIEW = Review.builder()
		.id(5L)
		.build();

	private final ReviewPhotoRepository reviewPhotoRepository = mock(ReviewPhotoRepository.class);
	private ReviewPhotoService reviewPhotoService;
	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;
	@Value("${cloud.aws.s3.accessKey}")
	private String accessKey;
	@Value("${cloud.aws.s3.secret}")
	private String secretKey;

	@BeforeEach
	void setUp() {
		reviewPhotoService = new ReviewPhotoService(reviewPhotoRepository, bucketName, accessKey, secretKey);
	}

	@Nested
	@DisplayName("createReviewPhoto 메서드는")
	class Describe_createReviewPhoto {
		@BeforeEach
		void setUp() {
			given(reviewPhotoRepository.saveAll(any(List.class))).will(invocation -> {
				List<ReviewPhoto> source = invocation.getArgument(0);
				List<ReviewPhoto> target = new LinkedList<>();
				long id = 1L;
				for (ReviewPhoto reviewPhoto : source) {
					target.add(ReviewPhoto.builder()
						.id(id)
						.review(reviewPhoto.getReview())
						.url(reviewPhoto.getUrl())
						.build());
					id += 1;
				}
				return target;
			});
		}
		
	}
}
