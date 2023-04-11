package com.glimps.glimpsserver.perfume.infra;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.fasterxml.uuid.Generators;
import com.glimps.glimpsserver.config.EntityManagerConfig;
import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Note;
import com.glimps.glimpsserver.perfume.domain.Perfume;

@DataJpaTest
@Import(EntityManagerConfig.class)
class PerfumeCustomRepositoryImplTest {
	public static final UUID NO5_UUID = Generators.timeBasedGenerator().generate();
	public static final UUID CK_UUID = Generators.timeBasedGenerator().generate();
	@Autowired
	private BrandRepository brandRepository;
	@Autowired
	private PerfumeRepository perfumeRepository;
	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private PerfumeCustomRepository perfumeCustomRepository;

	@BeforeEach
	void setUp() {
		Brand CK = Brand.builder().brandName("CK").build();
		Brand Chanel = Brand.builder().brandName("Chanel").build();

		Perfume no5 = Perfume.builder()
			.uuid(NO5_UUID)
			.brand(CK)
			.perfumeName("No.5")
			.overallRatings(4.2)
			.longevityRatings(3.2)
			.sillageRatings(4.0)
			.reviewCnt(420)
			.build();

		Perfume one = Perfume.builder()
			.uuid(CK_UUID)
			.brand(Chanel)
			.perfumeName("ONE")
			.overallRatings(4.5)
			.longevityRatings(4.1)
			.sillageRatings(3.2)
			.reviewCnt(900)
			.build();

		Note pineapple = Note.builder().korName("파인애플").engName("PINEAPPLE").build();
		Note lemon = Note.builder().korName("레몬").engName("LEMON").build();
		Note musk = Note.builder().korName("머스크").engName("MUSK").build();
		Note bergamot = Note.builder().korName("베르가못").engName("BERGAMOT").build();

		brandRepository.save(CK);
		brandRepository.save(Chanel);
		noteRepository.save(lemon);
		noteRepository.save(pineapple);
		noteRepository.save(musk);
		noteRepository.save(bergamot);

		no5.addNote(bergamot);
		no5.addNote(lemon);
		no5.addNote(musk);
		perfumeRepository.save(no5);

		one.addNote(pineapple);
		one.addNote(lemon);
		one.addNote(musk);
		perfumeRepository.save(one);

	}

	@Test
	@DisplayName("불러올 향수의 개수 N을 전달하면 N개의 향수를 총점순으로 불러온다.")
	void given_Amount_When_BestPerfume_Then_OrderByOverall() {
		//when
		List<Perfume> twoPerfume = perfumeCustomRepository.findOrderByOverall(2);
		List<Perfume> onePerfume = perfumeCustomRepository.findOrderByOverall(1);

		//then
		assertThat(twoPerfume).hasSize(2);
		assertThat(onePerfume).hasSize(1);
		assertThat(twoPerfume.get(0).getOverallRatings())
			.isGreaterThanOrEqualTo(twoPerfume.get(1).getOverallRatings());
	}

}
