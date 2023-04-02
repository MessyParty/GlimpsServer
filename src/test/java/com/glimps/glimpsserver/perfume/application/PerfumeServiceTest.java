package com.glimps.glimpsserver.perfume.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.uuid.Generators;
import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Note;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.dto.PerfumeResponse;
import com.glimps.glimpsserver.perfume.infra.PerfumeRepository;

@ExtendWith(MockitoExtension.class)
class PerfumeServiceTest {
	public static final UUID PERFUME_UUID = Generators.timeBasedGenerator().generate();
	public static final UUID NOT_EXIST_UUID = Generators.timeBasedGenerator().generate();
	public static final String PERFUME_NAME = "ONE";
	public static final String BRAND_NAME = "CK";
	public static final Double OVERALL = 4.7;
	public static final Double LONGEVITY = 4.7;
	public static final Double SILLAGE = 4.7;
	public static final int REVIEW_CNT = 420;

	public static final Brand CK = Brand.builder().brandName(BRAND_NAME).build();

	public static final Perfume PERFUME = Perfume.builder()
		.uuid(PERFUME_UUID)
		.brand(CK)
		.perfumeName(PERFUME_NAME)
		.overallRatings(OVERALL)
		.longevityRatings(LONGEVITY)
		.sillageRatings(SILLAGE)
		.reviewCnt(REVIEW_CNT)
		.build();

	public static final Note PINEAPPLE = Note.builder().korName("파인애플").engName("PINEAPPLE").build();
	public static final Note LEMON = Note.builder().korName("레몬").engName("LEMON").build();
	public static final Note MUSK = Note.builder().korName("머스크").engName("MUSK").build();


	@Mock
	private PerfumeRepository perfumeRepository;

	@InjectMocks
	private PerfumeService perfumeService;

	@BeforeAll
	static void setUp() {
		PERFUME.addNote(PINEAPPLE);
		PERFUME.addNote(LEMON);
		PERFUME.addNote(MUSK);
	}



	@Test
	@DisplayName("유효한 UUID로 조회 시 향수 정보 DTO 반환")
	void given_ValidUUID_When_find_Then_Success() {
	    //given
		given(perfumeRepository.findPerfumeWithEntities(PERFUME_UUID)).willReturn(Optional.of(PERFUME));

	    //when
		PerfumeResponse perfumeResponse = perfumeService.getPerfumeWithNotesAndBrand(PERFUME_UUID);

		//then
		assertThat(perfumeResponse.getUuid()).isEqualTo(PERFUME_UUID);
		assertThat(perfumeResponse.getPerfumeName()).isEqualTo(PERFUME_NAME);
		assertThat(perfumeResponse.getBrandName()).isEqualTo(BRAND_NAME);
		assertThat(perfumeResponse.getOverallRatings()).isEqualTo(OVERALL);
		assertThat(perfumeResponse.getLongevityRatings()).isEqualTo(LONGEVITY);
		assertThat(perfumeResponse.getSillageRatings()).isEqualTo(SILLAGE);
		assertThat(perfumeResponse.getReviewCnt()).isEqualTo(REVIEW_CNT);
		assertThat(perfumeResponse.getNotes()).hasSize(3);
	}

	@Test
	@DisplayName("유효하지 않은 UUID로 조회시 에러")
	void given_InvalidUUID() {
		//given
		given(perfumeRepository.findPerfumeWithEntities(NOT_EXIST_UUID)).willReturn(Optional.empty());

		//then
		assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
			//when
			perfumeService.getPerfumeWithNotesAndBrand(NOT_EXIST_UUID);
		});
	}



}
