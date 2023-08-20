package com.glimps.glimpsserver.review.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.uuid.Generators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ReviewPhoto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "uuid", nullable = false, columnDefinition = "BINARY(16)")
	private UUID uuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private Review review;

	private String url;

	public static ReviewPhoto createReviewPhoto(Review review, String url) {
		ReviewPhoto reviewPhoto = ReviewPhoto.builder()
			.review(review)
			.uuid(Generators.timeBasedGenerator().generate())
			.url(url)
			.build();

		review.addPhoto(reviewPhoto);
		return reviewPhoto;
	}
}
