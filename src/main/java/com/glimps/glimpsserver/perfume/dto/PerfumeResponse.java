package com.glimps.glimpsserver.perfume.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Note;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PerfumeResponse {


	private UUID uuid;

	private Long brandId;
	private String brandName;
	private String perfumeName;

	private double overallRatings;
	private double longevityRatings;
	private double sillageRatings;
	private int reviewCnt;

	private List<NoteResponse> notes = new ArrayList<>();


	public static PerfumeResponse of(Perfume perfume, List<Note> notes) {
		return PerfumeResponse.builder()
			.uuid(perfume.getUuid())
			.brandId(perfume.getBrand().getId())
			.brandName(perfume.getBrand().getBrandName())
			.perfumeName(perfume.getPerfumeName())
			.overallRatings(perfume.getOverallRatings())
			.longevityRatings(perfume.getLongevityRatings())
			.sillageRatings(perfume.getSillageRatings())
			.reviewCnt(perfume.getReviewCnt())
			.notes(notes.stream().map(NoteResponse::of).collect(Collectors.toList()))
			.build();
	}

	public static PerfumeResponse of(Perfume perfume) {
		return PerfumeResponse.builder()
			.uuid(perfume.getUuid())
			.brandId(perfume.getBrand().getId())
			.brandName(perfume.getBrand().getBrandName())
			.perfumeName(perfume.getPerfumeName())
			.overallRatings(perfume.getOverallRatings())
			.longevityRatings(perfume.getLongevityRatings())
			.sillageRatings(perfume.getSillageRatings())
			.reviewCnt(perfume.getReviewCnt())
			.build();
	}

	@QueryProjection
	public PerfumeResponse(Perfume perfume, Brand brand, List<Note> notes) {
		this.uuid = perfume.getUuid();
		this.brandId = brand.getId();
		this.brandName = brand.getBrandName();
		this.perfumeName = perfume.getPerfumeName();
		this.overallRatings = perfume.getOverallRatings();
		this.longevityRatings = perfume.getLongevityRatings();
		this.sillageRatings = perfume.getSillageRatings();
		this.reviewCnt = perfume.getReviewCnt();
		notes.forEach(n -> this.notes.add(NoteResponse.of(n)));
	}
}
