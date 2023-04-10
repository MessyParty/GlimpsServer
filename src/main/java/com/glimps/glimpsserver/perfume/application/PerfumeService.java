package com.glimps.glimpsserver.perfume.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.infra.PerfumeRepository;
import com.glimps.glimpsserver.review.dto.ReviewCreateRequest;
import com.glimps.glimpsserver.review.dto.ReviewUpdateRequest;
import com.glimps.glimpsserver.review.vo.ReviewRatings;

@Service
@Transactional(readOnly = true)
public class PerfumeService {
	private final PerfumeRepository perfumeRepository;

	public PerfumeService(PerfumeRepository perfumeRepository) {
		this.perfumeRepository = perfumeRepository;
	}

	@Transactional
	public void updateRatings(Perfume perfume, ReviewCreateRequest reviewCreateRequest) {
		perfume.updateRatings(reviewCreateRequest.getOverallRatings(), reviewCreateRequest.getLongevityRatings(),
			reviewCreateRequest.getSillageRatings());
	}

	@Transactional
	public void updateRatings(Perfume perfume, ReviewUpdateRequest reviewUpdateRequest, ReviewRatings reviewRatings) {
		perfume.updateRatings(reviewUpdateRequest.getOverallRatings(), reviewUpdateRequest.getLongevityRatings(),
			reviewUpdateRequest.getSillageRatings(), reviewRatings);
	}

	@Transactional
	public void updateRatings(Perfume perfume, ReviewRatings reviewRatings) {
		perfume.updateRatings(reviewRatings);
	}

	public Perfume getPerfumeById(UUID uuid) {
		return findPerfume(uuid);
	}

	private Perfume findPerfume(UUID perfumeUuid) {
		return perfumeRepository.findByUuid(perfumeUuid)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFUME_NOT_FOUND, perfumeUuid));
	}
}
