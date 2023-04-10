package com.glimps.glimpsserver.review.application;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.domain.ReviewPhoto;
import com.glimps.glimpsserver.review.infra.ReviewPhotoRepository;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ReviewPhotoService {
	private final ReviewPhotoRepository reviewPhotoRepository;
	private final String bucketName;
	private final String accessKey;
	private final String secretKey;

	public ReviewPhotoService(
		ReviewPhotoRepository reviewPhotoRepository,
		@Value("${cloud.aws.s3.bucket}") String bucketName,
		@Value("${cloud.aws.s3.accessKey}") String accessKey,
		@Value("${cloud.aws.s3.secretKey}") String secretKey) {
		this.reviewPhotoRepository = reviewPhotoRepository;
		this.bucketName = bucketName;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
	}

	public List<ReviewPhoto> createReviewPhotos(Review review, List<String> photoUrls) {
		List<ReviewPhoto> photos = new LinkedList<>();
		for (String url : photoUrls) {
			photos.add(ReviewPhoto.createReviewPhoto(review, url));
		}
		return reviewPhotoRepository.saveAll(photos);
	}

	public List<ReviewPhoto> updateReviewPhotos(Review review, List<String> photoUrls) {
		review.getReviewPhotos().clear();
		return createReviewPhotos(review, photoUrls);
	}

	public void deleteReviewPhotos(Review review) {
		reviewPhotoRepository.deleteAll(review.getReviewPhotos());
	}

	public List<ReviewPhoto> addPhoto(final Review review, List<MultipartFile> files) throws IOException {
		List<String> photoUrls = uploadFilesToS3(files);

		return createReviewPhotos(review, photoUrls);
	}

	private List<String> uploadFilesToS3(List<MultipartFile> files) throws IOException {
		List<String> fileUrls = new LinkedList<>();
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
			.withRegion(Regions.AP_NORTHEAST_2)
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.build();

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			String key = UUID.randomUUID() + "/" + fileName;

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(file.getSize());
			metadata.setContentType(file.getContentType());

			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead);

			s3Client.putObject(putObjectRequest);
			fileUrls.add(s3Client.getUrl(bucketName, key).toString());
		}
		return fileUrls;
	}

}
