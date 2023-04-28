package com.glimps.glimpsserver.perfume.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.glimps.glimpsserver.review.vo.ReviewRatings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "perfume")
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Perfume {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "uuid", nullable = false, columnDefinition = "BINARY(16)")
	private UUID uuid;

	@JoinColumn(name = "brand_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Brand brand;

	private String perfumeName;

	private double overallRatings;
	private double longevityRatings;
	private double sillageRatings;
	private double scentRatings;


	private int reviewCnt;

	@OneToMany(mappedBy = "perfume", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<PerfumeNote> perfumeNotes = new ArrayList<>();

	@OneToMany(mappedBy = "perfume", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<PerfumePhoto> perfumePhotos = new ArrayList<>();

	@Builder
	public Perfume(UUID uuid, Brand brand, String perfumeName, double overallRatings, double longevityRatings,
		double sillageRatings, int reviewCnt) {
		this.uuid = uuid;
		this.brand = brand;
		this.perfumeName = perfumeName;
		this.overallRatings = overallRatings;
		this.longevityRatings = longevityRatings;
		this.sillageRatings = sillageRatings;
		this.reviewCnt = reviewCnt;
	}

	public static Perfume createPerfume(Brand brand, String perfumeName) {
		return Perfume.builder()
			.uuid(UUID.randomUUID())
			.brand(brand)
			.perfumeName(perfumeName)
			.overallRatings(0)
			.longevityRatings(0)
			.sillageRatings(0)
			.reviewCnt(0)
			.build();
	}

	public void updateRatings(double overallRatings, double longevityRatings, double sillageRatings) {
		this.overallRatings = (this.overallRatings * reviewCnt + overallRatings) / (reviewCnt + 1);
		this.longevityRatings = (this.longevityRatings * reviewCnt + longevityRatings) / (reviewCnt + 1);
		this.sillageRatings = (this.sillageRatings * reviewCnt + sillageRatings) / (reviewCnt + 1);
		this.scentRatings = (this.scentRatings * reviewCnt + sillageRatings) / (reviewCnt + 1);
		increaseReviewCount();
	}

	public void updateRatings(double overallRatings, double longevityRatings, double sillageRatings,
		ReviewRatings reviewRatings) {
		this.overallRatings =
			(this.overallRatings * reviewCnt + overallRatings - reviewRatings.getOverallRatings()) / reviewCnt;
		this.longevityRatings =
			(this.longevityRatings * reviewCnt + longevityRatings - reviewRatings.getLongevityRatings()) / reviewCnt;
		this.sillageRatings =
			(this.sillageRatings * reviewCnt + sillageRatings - reviewRatings.getSillageRatings()) / reviewCnt;
		this.scentRatings =
			(this.scentRatings * reviewCnt + sillageRatings - reviewRatings.getScentRatings()) / reviewCnt;
	}

	public void updateRatings(ReviewRatings reviewRatings) {
		this.overallRatings = (this.overallRatings * reviewCnt - reviewRatings.getOverallRatings()) / (reviewCnt - 1);
		this.longevityRatings =
			(this.longevityRatings * reviewCnt - reviewRatings.getLongevityRatings()) / (reviewCnt - 1);
		this.sillageRatings = (this.sillageRatings * reviewCnt - reviewRatings.getSillageRatings()) / (reviewCnt - 1);
		this.scentRatings = (this.scentRatings * reviewCnt - reviewRatings.getScentRatings()) / (reviewCnt - 1);
		decreaseReviewCount();
	}

	private void increaseReviewCount() {
		this.reviewCnt++;
	}

	private void decreaseReviewCount() {
		this.reviewCnt--;
	}

	public void addNote(Note note) {
		this.perfumeNotes.add(PerfumeNote.mapNoteToPerfume(this, note));
	}

	public void addPhoto(PerfumePhoto photo) {
		this.perfumePhotos.add(photo);
		photo.mapTo(this);
	}

	public void removeNote(Note note) {
		this.perfumeNotes.stream()
			.filter(pn -> pn.getNote().getId().equals(note.getId()))
			.collect(Collectors.toList())
			.forEach(n -> this.perfumeNotes.remove(n));
	}
}
