package com.glimps.glimpsserver.perfume.infra;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PerfumeCustomRepositoryImpl implements PerfumeCustomRepository {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Perfume> searchByBrand(String brandName, Pageable pageable) {
		return null;
	}

	@Override
	public List<Perfume> getRandom(Integer amount) {
		return null;
	}

	@Override
	public List<Perfume> findOrderByOverall(Integer amount) {
		return em.createQuery("select p from Perfume p "
			+ "join fetch p.brand b "
			+ "order by p.overallRatings desc ", Perfume.class).setMaxResults(amount)
			.getResultList();
	}

}
