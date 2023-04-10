package com.glimps.glimpsserver.review.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.glimps.glimpsserver.user.domain.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_hearts")
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReviewHeart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private Review review;

	public static ReviewHeart createReviewHeart(Review review, User user) {
		review.increaseHeartCnt();
		return ReviewHeart.builder()
			.user(user)
			.review(review)
			.build();
	}

	public void cancelReviewHeart() {
		this.review.decreaseHeartCnt();
	}
}
