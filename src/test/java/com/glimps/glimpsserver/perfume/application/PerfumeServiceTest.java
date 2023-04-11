package com.glimps.glimpsserver.perfume.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
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
import com.glimps.glimpsserver.common.error.CustomException;
import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Note;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.dto.PerfumeResponse;
import com.glimps.glimpsserver.perfume.infra.PerfumeCustomRepository;
import com.glimps.glimpsserver.perfume.infra.PerfumeRepository;

@ExtendWith(MockitoExtension.class)
class PerfumeServiceTest {
	public static final UUID PERFUME1_UUID = Generators.timeBasedGenerator().generate();
	public static final UUID PERFUME2_UUID = Generators.timeBasedGenerator().generate();
	public static final UUID NOT_EXIST_UUID = Generators.timeBasedGenerator().generate();
	public static final String PERFUME_NAME1 = "ONE";
	public static final String BRAND_NAME1 = "CK";
	public static final String PERFUME_NAME2 = "No.5";
	public static final String BRAND_NAME2 = "Chanel";
	public static final Double OVERALL = 4.7;
	public static final Double LONGEVITY = 4.7;
	public static final Double SILLAGE = 4.7;
	public static final int REVIEW_CNT = 420;

	public static final Brand CK = Brand.builder().brandName(BRAND_NAME1).build();
	public static final Brand CHANEL = Brand.builder().brandName(BRAND_NAME2).build();

	public static final Perfume ONE = Perfume.builder()
		.uuid(PERFUME1_UUID)
		.brand(CK)
		.perfumeName(PERFUME_NAME1)
		.overallRatings(OVERALL)
		.longevityRatings(LONGEVITY)
		.sillageRatings(SILLAGE)
		.reviewCnt(REVIEW_CNT)
		.build();

	public static final Perfume NO5 = Perfume.builder()
		.uuid(PERFUME2_UUID)
		.brand(CK)
		.perfumeName(PERFUME_NAME2)
		.overallRatings(OVERALL)
		.longevityRatings(LONGEVITY)
		.sillageRatings(SILLAGE)
		.reviewCnt(REVIEW_CNT)
		.build();

	public static final Note PINEAPPLE = Note.builder().korName("파인애플").engName("PINEAPPLE").build();
	public static final Note LEMON = Note.builder().korName("레몬").engName("LEMON").build();
	public static final Note MUSK = Note.builder().korName("머스크").engName("MUSK").build();
	public static final Note BERGAMOT = Note.builder().korName("베르가못").engName("BERGAMOT").build();

	@Mock
	private PerfumeRepository perfumeRepository;

	@Mock
	private PerfumeCustomRepository perfumeCustomRepository;

	@InjectMocks
	private PerfumeService perfumeService;

	@BeforeAll
	static void setUp() {
		ONE.addNote(PINEAPPLE);
		ONE.addNote(LEMON);
		ONE.addNote(MUSK);

		NO5.addNote(BERGAMOT);
		NO5.addNote(LEMON);
		NO5.addNote(MUSK);
	}

	@Test
	@DisplayName("유효한 UUID로 조회 시 향수 정보 DTO 반환")
	void given_ValidUUID_When_find_Then_Success() {
		//given
		given(perfumeRepository.findPerfumeWithEntities(PERFUME1_UUID)).willReturn(Optional.of(ONE));

		//when
		PerfumeResponse perfumeResponse = perfumeService.getPerfumeWithNotesAndBrand(PERFUME1_UUID);

		//then
		assertThat(perfumeResponse.getUuid()).isEqualTo(PERFUME1_UUID);
		assertThat(perfumeResponse.getPerfumeName()).isEqualTo(PERFUME_NAME1);
		assertThat(perfumeResponse.getBrandName()).isEqualTo(BRAND_NAME1);
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

	@Test
	@DisplayName("전달된 개수만큼의 향수를 조회하면, 향수 정보를 Dto로 변환한다.")
	void given_ValidAmount() {
		//given
		given(perfumeCustomRepository.findOrderByOverall(2)).willReturn(List.of(ONE, NO5));

		//when
		List<PerfumeResponse> result = perfumeService.getPerfumeByOverall(2);

		//then
		assertThat(result).hasSize(2);
		assertThat(result.get(0)).isNotNull();
		assertThat(result.get(1)).isNotNull();
		assertThat(List.of(result.get(0).getUuid(), result.get(1).getUuid()))
			.containsAll(List.of(PERFUME1_UUID, PERFUME2_UUID));
	}

	@Test
	@DisplayName("총점순 조회는 10개를 초과해 조회할 시 예외를 던진다.")
	void given_InValidAmount() {
		//given
		Integer invalidAmount = 11;

		//then
		assertThatExceptionOfType(CustomException.class).isThrownBy(() -> {
			perfumeService.getPerfumeByOverall(invalidAmount);
		}).withMessage(ErrorCode.PERFUME_TOO_MANY_AMOUNT.getMessage());
		//when

	}

}
