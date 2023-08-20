package com.glimps.glimpsserver.perfume.infra;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.dto.PerfumeResponse;
import com.glimps.glimpsserver.perfume.dto.PerfumeSearchCondition;

public interface PerfumeCustomRepository {
	Slice<Perfume> searchByBrand(String brandName, Pageable pageable);

	List<Perfume> findRandom(Integer amount);

	List<Perfume> findOrderByOverall(Integer amount);

	Slice<PerfumeResponse> searchByCondition(PerfumeSearchCondition condition, Pageable pageable);
}
