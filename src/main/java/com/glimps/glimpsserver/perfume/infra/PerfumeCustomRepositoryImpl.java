package com.glimps.glimpsserver.perfume.infra;

import static com.glimps.glimpsserver.perfume.domain.QBrand.*;
import static com.glimps.glimpsserver.perfume.domain.QNote.*;
import static com.glimps.glimpsserver.perfume.domain.QPerfume.*;
import static com.glimps.glimpsserver.perfume.domain.QPerfumeNote.*;
import static com.glimps.glimpsserver.perfume.domain.QPerfumePhoto.*;
import static com.querydsl.core.group.GroupBy.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.dto.PerfumeResponse;
import com.glimps.glimpsserver.perfume.dto.PerfumeSearchCondition;
import com.glimps.glimpsserver.perfume.dto.QPerfumeResponse;
import com.querydsl.core.BooleanBuilder;
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
		List<Perfume> content = queryFactory.selectFrom(perfume)
			.leftJoin(perfume.brand, brand)
			.fetchJoin()
			.where(brandLike(brandName))
			.offset(pageable.getOffset())
			.limit((long)pageable.getPageSize() + 1)
			.fetch();

		return getSlice(pageable, content);
	}

	@Override
	public List<Perfume> findRandom(Integer amount) {
		String query = "SELECT * FROM perfume "
			+ "ORDER BY rand() limit :amount";
		return em.createNativeQuery(query, Perfume.class)
			.setParameter("amount", amount)
			.getResultList();
	}

	@Override
	public List<Perfume> findOrderByOverall(Integer amount) {
		return em.createQuery("select p from Perfume p "
				+ "join fetch p.brand b "
				+ "order by p.overallRatings desc ", Perfume.class).setMaxResults(amount)
			.getResultList();
	}

	@Override
	public Slice<PerfumeResponse> searchByCondition(PerfumeSearchCondition condition, Pageable pageable) {
		List<PerfumeResponse> content = queryFactory
			.from(perfume)
			.leftJoin(perfume.brand, brand)
			.leftJoin(perfume.perfumePhotos, perfumePhoto)
			.leftJoin(perfume.perfumeNotes, perfumeNote)
			.leftJoin(perfumeNote.note, note)
			.where(
				perfumeLike(condition.getPerfumeName())
				, brandLike(condition.getBrandName())
				, noteLike(condition.getNoteName()))
			.offset(pageable.getOffset())
			.limit((long)pageable.getPageSize() + 1)
			.transform(groupBy(perfume.id).list(
				new QPerfumeResponse(
					perfume,
					brand,
					list(note),
					list(perfumePhoto)
				)
			));

		return getSlice(pageable, content);
	}

	private BooleanBuilder perfumeLike(String perfumeName) {
		if (perfumeName == null)
			return new BooleanBuilder();
		return new BooleanBuilder(perfume.perfumeName.startsWith(perfumeName));
	}

	private BooleanBuilder brandLike(String brandName) {
		if (brandName == null)
			return new BooleanBuilder();
		else if (brandName.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*"))
			return new BooleanBuilder(brand.brandNameKor.startsWith(brandName));

		return new BooleanBuilder(brand.brandNameEng.startsWith(brandName));
	}

	private BooleanBuilder noteLike(String noteName) {
		if (noteName == null)
			return new BooleanBuilder();
		else if (noteName.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*"))
			return new BooleanBuilder(note.korName.startsWith(noteName));

		return new BooleanBuilder(note.engName.startsWith(noteName.toUpperCase()));
	}

	private static <T> Slice<T> getSlice(Pageable pageable, List<T> content) {
		boolean hasNext = false;

		if (content.size() > pageable.getPageSize()) {
			content.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(content, pageable, hasNext);
	}

}
