package com.glimps.glimpsserver.perfume.infra;

import static com.glimps.glimpsserver.perfume.domain.QBrand.*;
import static com.glimps.glimpsserver.perfume.domain.QPerfume.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class PerfumeCustomRepositoryImpl implements PerfumeCustomRepository {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<Perfume> searchByBrand(String brandName, Pageable pageable) {
		List<Perfume> fetch = queryFactory.selectFrom(perfume)
			.leftJoin(perfume.brand, brand)
			.where(brand.brandName.eq(brandName))
			.offset(pageable.getOffset())
			.limit((long) pageable.getPageSize() + 1)
			.fetch();

		return new SliceImpl<>(fetch, pageable, true);
	}

	@Override
	public List<Perfume> findRandom(Integer amount) {
		String query = "SELECT * FROM PERFUME "
			+ "ORDER BY rand() limit " + amount;
		return em.createNativeQuery(query, Perfume.class).getResultList();
	}

	@Override
	public List<Perfume> findOrderByOverall(Integer amount) {
		return em.createQuery("select p from Perfume p "
			+ "join fetch p.brand b "
			+ "order by p.overallRatings desc ", Perfume.class).setMaxResults(amount)
			.getResultList();
	}

}
