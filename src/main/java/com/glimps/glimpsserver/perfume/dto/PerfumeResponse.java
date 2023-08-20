package com.glimps.glimpsserver.perfume.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Note;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.domain.PerfumePhoto;
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
	private String brandNameKor;

	private String perfumeName;
	private String introduction;
	private double overallRatings;
	private double scentRatings;
	private double longevityRatings;
	private double sillageRatings;
	private int reviewCnt;

	private List<NoteResponse> notes = new ArrayList<>();
	private List<PerfumePhotoResponse> photos = new ArrayList<>();

	public static PerfumeResponse of(Perfume perfume) {
		return PerfumeResponse.of(perfume, new ArrayList<>());
	}

	public static PerfumeResponse of(Perfume perfume, List<Note> notes) {
		return PerfumeResponse.builder()
			.uuid(perfume.getUuid())
			.brandId(perfume.getBrand().getId())
			.brandName(perfume.getBrand().getBrandNameEng())
			.brandNameKor(perfume.getBrand().getBrandNameKor())
			.perfumeName(perfume.getPerfumeName())
			.introduction(perfume.getIntroduction())
			.overallRatings(perfume.getOverallRatings())
			.scentRatings(perfume.getScentRatings())
			.longevityRatings(perfume.getLongevityRatings())
			.sillageRatings(perfume.getSillageRatings())
			.reviewCnt(perfume.getReviewCnt())
			.photos(perfume.getPerfumePhotos().stream().map(PerfumePhotoResponse::of).collect(Collectors.toList()))
			.notes(notes.stream().map(NoteResponse::of).collect(Collectors.toList()))
			.build();
	}



	@QueryProjection
	public PerfumeResponse(Perfume perfume, Brand brand, List<Note> notes, List<PerfumePhoto> photos) {
		this.uuid = perfume.getUuid();
		this.brandId = brand.getId();
		this.brandName = brand.getBrandNameEng();
		this.brandNameKor = brand.getBrandNameKor();
		this.perfumeName = perfume.getPerfumeName();
		this.introduction = perfume.getIntroduction();
		this.overallRatings = perfume.getOverallRatings();
		this.scentRatings = perfume.getScentRatings();
		this.longevityRatings = perfume.getLongevityRatings();
		this.sillageRatings = perfume.getSillageRatings();
		this.reviewCnt = perfume.getReviewCnt();
		notes.forEach(n -> this.notes.add(NoteResponse.of(n)));
		photos.forEach(photo -> this.photos.add(PerfumePhotoResponse.of(photo)));
	}
}
