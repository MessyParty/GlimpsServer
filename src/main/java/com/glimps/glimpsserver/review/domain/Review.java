package com.glimps.glimpsserver.review.domain;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.uuid.Generators;
import com.glimps.glimpsserver.common.domain.BaseTimeEntity;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.review.dto.ReviewCreateRequest;
import com.glimps.glimpsserver.review.dto.ReviewUpdateRequest;
import com.glimps.glimpsserver.user.domain.User;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "uuid", nullable = false, columnDefinition = "BINARY(16)")
	private UUID uuid;

	@Column(name = "title")
	private String title;

	@Column(name = "body")
	private String body;

	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@JoinColumn(name = "perfume_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Perfume perfume;

	@Builder.Default
	@OneToMany(mappedBy = "review")
	private List<ReviewPhoto> reviewPhotos = Lists.newArrayList();

	@Column(name = "hearts_cnt")
	private int heartsCnt;

	private double overallRatings;

	private double longevityRatings;

	private double sillageRatings;

	public static Review createReview(ReviewCreateRequest reviewCreateRequest, User user, Perfume perfume) {
		user.addReviewCnt();
		return Review.builder()
			.uuid(Generators.timeBasedGenerator().generate())
			.title(reviewCreateRequest.getTitle())
			.body(reviewCreateRequest.getBody())
			.user(user)
			.perfume(perfume)
			.overallRatings(reviewCreateRequest.getOverallRatings())
			.longevityRatings(reviewCreateRequest.getLongevityRatings())
			.sillageRatings(reviewCreateRequest.getSillageRatings())
			.build();
	}

	protected void addPhoto(ReviewPhoto photo) {
		if (!this.reviewPhotos.contains(photo)) {
			this.reviewPhotos.add(photo);
		}
	}

	protected void increaseHeartCnt() {
		this.heartsCnt++;
	}

	public void decreaseHeartCnt() {
		if (this.heartsCnt != 0) {
			this.heartsCnt--;
		}
	}

	public boolean authorize(User user) {
		return this.user.getEmail().equals(user.getEmail());
	}

	public void updateReview(ReviewUpdateRequest reviewUpdateRequest) {
		this.title = reviewUpdateRequest.getTitle() == null ? this.title : reviewUpdateRequest.getTitle();
		this.body = reviewUpdateRequest.getBody() == null ? this.body : reviewUpdateRequest.getBody();
		this.overallRatings = reviewUpdateRequest.getOverallRatings() == null ? this.overallRatings :
			reviewUpdateRequest.getOverallRatings();
		this.longevityRatings =
			reviewUpdateRequest.getLongevityRatings() == null ? this.longevityRatings : reviewUpdateRequest
				.getLongevityRatings();
		this.sillageRatings = reviewUpdateRequest.getSillageRatings() == null ? this.sillageRatings : reviewUpdateRequest
			.getSillageRatings();
	}
}
