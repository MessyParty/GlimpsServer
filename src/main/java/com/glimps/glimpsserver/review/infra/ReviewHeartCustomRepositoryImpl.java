package com.glimps.glimpsserver.review.infra;

import static com.glimps.glimpsserver.review.domain.QReviewHeart.*;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.glimps.glimpsserver.review.domain.QReview;
import com.glimps.glimpsserver.review.domain.ReviewHeart;
import com.glimps.glimpsserver.user.domain.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewHeartCustomRepositoryImpl implements ReviewHeartCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<ReviewHeart> findByReviewAndUser(UUID reviewUuid, Long userId) {
		return jpaQueryFactory.selectFrom(reviewHeart)
			.fetchJoin().leftJoin(QUser.user)
			.fetchJoin().leftJoin(QReview.review)
			.where(QReview.review.uuid.eq(reviewUuid))
			.where(QUser.user.id.eq(userId))
			.stream().findFirst();
	}
}
