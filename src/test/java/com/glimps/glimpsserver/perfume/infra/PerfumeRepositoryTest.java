package com.glimps.glimpsserver.perfume.infra;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.fasterxml.uuid.Generators;
import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Note;
import com.glimps.glimpsserver.perfume.domain.Perfume;

@DataJpaTest
class PerfumeRepositoryTest {

	public static final UUID PERFUME_UUID = Generators.timeBasedGenerator().generate();
	public static final UUID NOT_EXIST_UUID = Generators.timeBasedGenerator().generate();
	public static final String PERFUME_NAME = "ONE";
	public static final String BRAND_NAME = "CK";
	public static final Double OVERALL = 4.7;
	public static final Double LONGEVITY = 4.7;
	public static final Double SILLAGE = 4.7;
	public static final int REVIEW_CNT = 420;

	@Autowired
	private BrandRepository brandRepository;
	@Autowired
	private PerfumeRepository perfumeRepository;
	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private PerfumeNoteRepository perfumeNoteRepository;

	@Autowired
	private TestEntityManager em;

	@BeforeEach
	void setUp() {
		Brand CK = Brand.builder().brandName(BRAND_NAME).build();

		Perfume perfume = Perfume.builder()
			.uuid(PERFUME_UUID)
			.brand(CK)
			.perfumeName(PERFUME_NAME)
			.overallRatings(OVERALL)
			.longevityRatings(LONGEVITY)
			.sillageRatings(SILLAGE)
			.reviewCnt(REVIEW_CNT)
			.build();

		Note PINEAPPLE = Note.builder().korName("파인애플").engName("PINEAPPLE").build();
		Note LEMON = Note.builder().korName("레몬").engName("LEMON").build();
		Note MUSK = Note.builder().korName("머스크").engName("MUSK").build();



		brandRepository.save(CK);
		noteRepository.save(LEMON);
		noteRepository.save(PINEAPPLE);
		noteRepository.save(MUSK);

		Perfume savedPerfume = perfumeRepository.save(perfume);
		savedPerfume.addNote(LEMON);
		savedPerfume.addNote(PINEAPPLE);
		savedPerfume.addNote(MUSK);
	}

	@Test
	@DisplayName("유효한 UUID로 조회 시 향수 정보 반환")
	void give_ValidUUID_When_FindPerfume_Then_Success() {
		//when
		Optional<Perfume> OptionalPerfume = perfumeRepository.findPerfumeWithEntities(PERFUME_UUID);

		//then
		assertThat(OptionalPerfume.get()).isNotNull();
		Perfume perfume = OptionalPerfume.get();

		assertThat(perfume.getUuid()).isEqualTo(PERFUME_UUID);
		assertThat(perfume.getBrand().getBrandName()).isEqualTo(BRAND_NAME);
		assertThat(perfume.getPerfumeName()).isEqualTo(PERFUME_NAME);
		assertThat(perfume.getOverallRatings()).isEqualTo(OVERALL);
		assertThat(perfume.getLongevityRatings()).isEqualTo(LONGEVITY);
		assertThat(perfume.getSillageRatings()).isEqualTo(SILLAGE);
		assertThat(perfume.getReviewCnt()).isEqualTo(REVIEW_CNT);


		assertThat(perfume.getPerfumeNotes()).hasSize(3);

	}

	@Test
	@DisplayName("유효하지 않은 UUID로 조회시 null 반환")
	void give_InvalidUUID_When_FindPerfume_Then_Success() {
		//when
		Optional<Perfume> OptionalPerfume = perfumeRepository.findPerfumeWithEntities(NOT_EXIST_UUID);

		//then
		assertThat(OptionalPerfume).isEmpty();
	}

}
