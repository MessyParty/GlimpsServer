package com.glimps.glimpsserver.perfume.infra;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.glimps.glimpsserver.perfume.domain.Perfume;

public interface PerfumeCustomRepository {
	Page<Perfume> searchByBrand(String brandName, Pageable pageable);

	List<Perfume> getRandom(Integer amount);

	List<Perfume> findOrderByOverall(Integer amount);
}
