package com.glimps.glimpsserver.perfume.application;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.perfume.domain.Note;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.domain.PerfumeNote;
import com.glimps.glimpsserver.perfume.dto.PerfumeResponse;
import com.glimps.glimpsserver.perfume.infra.PerfumeNoteRepository;
import com.glimps.glimpsserver.perfume.infra.PerfumeRepository;
import com.glimps.glimpsserver.review.dto.ReviewCreateRequest;
import com.glimps.glimpsserver.review.dto.ReviewUpdateRequest;
import com.glimps.glimpsserver.review.vo.ReviewRatings;

@Service
@Transactional(readOnly = true)
public class PerfumeService {
	private final PerfumeRepository perfumeRepository;
	private final PerfumeNoteRepository perfumeNoteRepository;

	public PerfumeService(PerfumeRepository perfumeRepository, PerfumeNoteRepository perfumeNoteRepository) {
		this.perfumeRepository = perfumeRepository;
		this.perfumeNoteRepository = perfumeNoteRepository;
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

	public PerfumeResponse getPerfumeWithNotesAndBrand(UUID uuid) {
		Perfume perfume = findPerfumeWithEntities(uuid);
		List<Note> notes = perfume.getPerfumeNotes().stream().map(PerfumeNote::getNote).collect(Collectors.toList());
		return PerfumeResponse.of(perfume, notes);
	}

	private Perfume findPerfume(UUID perfumeUuid) {
		return perfumeRepository.findByUuid(perfumeUuid)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFUME_NOT_FOUND, perfumeUuid));
	}

	private Perfume findPerfumeWithEntities(UUID perfumeUuid) {
		return perfumeRepository.findPerfumeWithEntities(perfumeUuid)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFUME_NOT_FOUND, perfumeUuid));
	}

	public List<PerfumeResponse> getAll() {
		List<Perfume> perfumes = perfumeRepository.findAll();
		return perfumes.stream().map(PerfumeResponse::of).collect(Collectors.toList());
	}


}
