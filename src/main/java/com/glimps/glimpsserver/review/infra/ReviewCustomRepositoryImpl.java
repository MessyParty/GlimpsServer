package com.glimps.glimpsserver.review.infra;


import static com.glimps.glimpsserver.perfume.domain.QPerfume.perfume;
import static com.glimps.glimpsserver.review.domain.QReview.review;
import static com.glimps.glimpsserver.review.domain.QReviewPhoto.reviewPhoto;
import static com.glimps.glimpsserver.user.domain.QUser.user;

import com.glimps.glimpsserver.review.domain.Review;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Review> findTop10ByOrderByCreatedAtDesc() {
		return jpaQueryFactory.selectFrom(review)
			.innerJoin(user).fetchJoin()
			.on(user.id.eq(review.user.id))
			.innerJoin(perfume).fetchJoin()
			.on(perfume.id.eq(review.perfume.id))
			.leftJoin(reviewPhoto).fetchJoin()
			.on(reviewPhoto.review.id.eq(review.id))
			.orderBy(review.createdAt.desc())
			.limit(10)
			.stream().collect(Collectors.toList());
	}

	@Override
	public Page<Review> findAllByOrder(Pageable pageRequest) {
		JPAQuery<Review> joinQuery = jpaQueryFactory.selectFrom(review)
			.innerJoin(user).fetchJoin()
			.on(user.id.eq(review.user.id))
			.innerJoin(perfume).fetchJoin()
			.on(perfume.id.eq(review.perfume.id))
			.leftJoin(reviewPhoto).fetchJoin()
			.on(reviewPhoto.review.id.eq(review.id));

		long totalElements = joinQuery.stream().count();

		List<Review> result = joinQuery
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.orderBy(getSort(pageRequest))
			.stream().collect(Collectors.toList());

		return new PageImpl<>(result,pageRequest, totalElements);
	}

	@Override
	public Page<Review> findAllByUserId(Long userId, Pageable pageRequest) {
		JPAQuery<Review> joinQuery = jpaQueryFactory.selectFrom(review)
			.innerJoin(user).fetchJoin()
			.on(user.id.eq(review.user.id))
			.innerJoin(perfume).fetchJoin()
			.on(perfume.id.eq(review.perfume.id))
			.leftJoin(reviewPhoto).fetchJoin()
			.on(reviewPhoto.review.id.eq(review.id))
			.where(review.user.id.eq(userId));

		long totalElements = joinQuery.stream().count();

		List<Review> result = joinQuery
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.orderBy(getSort(pageRequest))
			.stream().collect(Collectors.toList());
		return new PageImpl<>(result, pageRequest, totalElements);
	}

	@Override
	public List<Review> findAllByPerfumeId(UUID perfumeUuid) {
		return jpaQueryFactory.selectFrom(review)
			.innerJoin(perfume).fetchJoin()
			.on(perfume.id.eq(review.perfume.id))
			.innerJoin(user).fetchJoin()
			.on(user.id.eq(review.user.id))
			.leftJoin(reviewPhoto).fetchJoin()
			.on(reviewPhoto.review.id.eq(review.id))
			.orderBy(getSort(Pageable.unpaged()))
			.where(perfume.uuid.eq(perfumeUuid))
			.stream().collect(Collectors.toList());
	}

	@Override
	public List<Review> findBestReviewByAmount(int amountOfBestReview) {
		return jpaQueryFactory.selectFrom(review)
			.innerJoin(user).fetchJoin()
			.on(user.id.eq(review.user.id))
			.innerJoin(perfume).fetchJoin()
			.on(perfume.id.eq(review.perfume.id))
			.leftJoin(reviewPhoto).fetchJoin()
			.on(reviewPhoto.review.id.eq(review.id))
			.orderBy(review.heartsCnt.desc())
			.limit(amountOfBestReview)
			.stream()
			.collect(Collectors.toList());
	}

	private OrderSpecifier<?> getSort(Pageable pageRequest) {
		for (Sort.Order order : pageRequest.getSort()) {
			Order direction = order.isAscending() ? Order.ASC : Order.DESC;
			switch (order.getProperty()) {
				case "heartsCnt":
					return new OrderSpecifier<>(direction, review.heartsCnt);
				default:
					return new OrderSpecifier<>(direction, review.createdAt);
			}
		}
		if (pageRequest.isUnpaged()) {
			return new OrderSpecifier<>(Order.DESC, review.createdAt);
		}
		return null;
	}

	@Override
	public Optional<Review> findByUuid(UUID uuid) {
		return jpaQueryFactory.selectFrom(review)
			.distinct()
			.innerJoin(user).fetchJoin()
			.on(user.id.eq(review.user.id))
			.innerJoin(perfume).fetchJoin()
			.on(perfume.id.eq(review.perfume.id))
			.leftJoin(reviewPhoto).fetchJoin()
			.on(reviewPhoto.review.id.eq(review.id))
			.where(review.uuid.eq(uuid))
			.stream()
			.findFirst();
	}
}
