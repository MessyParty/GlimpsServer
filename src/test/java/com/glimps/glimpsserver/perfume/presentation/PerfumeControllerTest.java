package com.glimps.glimpsserver.perfume.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.uuid.Generators;
import com.glimps.glimpsserver.common.config.SecurityConfig;
import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.perfume.application.PerfumeService;
import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.dto.NoteResponse;
import com.glimps.glimpsserver.perfume.dto.PerfumeResponse;

@WebMvcTest(controllers = PerfumeController.class, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class})})
@WithMockUser
class PerfumeControllerTest {

	public static final UUID PERFUME_UUID = Generators.timeBasedGenerator().generate();
	public static final UUID NOT_EXIST_UUID = Generators.timeBasedGenerator().generate();
	public static final Brand BRAND = Brand.builder().brandNameEng("CK").id(2L).build();
	public static final String PERFUME_NAME = "ONE";
	public static final Double OVERALL = 4.7;
	public static final Double LONGEVITY = 4.7;
	public static final Double SILLAGE = 4.7;
	public static final int REVIEW_CNT = 420;
	public static final NoteResponse PINEAPPLE = NoteResponse.builder()
		.id(3L)
		.korName("파인애플")
		.engName("PINEAPPLE")
		.build();
	public static final NoteResponse LEMON = NoteResponse.builder().id(5L).korName("레몬").engName("LEMON").build();
	public static final NoteResponse MUSK = NoteResponse.builder().id(10L).korName("머스크").engName("MUSK").build();
	public static final List<NoteResponse> notes = new ArrayList<>(List.of(PINEAPPLE, LEMON, MUSK));

	public static final PerfumeResponse PERFUME_DTO = PerfumeResponse.builder()
		.uuid(PERFUME_UUID)
		.brandId(BRAND.getId())
		.brandName(BRAND.getBrandNameEng())
		.perfumeName(PERFUME_NAME)
		.overallRatings(OVERALL)
		.longevityRatings(LONGEVITY)
		.sillageRatings(SILLAGE)
		.reviewCnt(REVIEW_CNT)
		.notes(notes)
		.build();

	@MockBean
	private PerfumeService perfumeService;
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("유효한 UUID로 접근 시 향수 정보를 반환한다.")
	void given_ValidUUID_When_GetPerfume_Then_Success() throws Exception {
		//given
		given(perfumeService.getPerfumeWithNotesAndBrand(PERFUME_UUID)).willReturn(PERFUME_DTO);

		//when
		mockMvc.perform(get("/perfumes/" + PERFUME_UUID))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.uuid").value(PERFUME_UUID.toString()))
			.andExpect(jsonPath("$.brandId").value(BRAND.getId()))
			.andExpect(jsonPath("$.brandName").value(BRAND.getBrandNameEng()))
			.andExpect(jsonPath("$.perfumeName").value(PERFUME_NAME))
			.andExpect(jsonPath("$.overallRatings").value(OVERALL))
			.andExpect(jsonPath("$.longevityRatings").value(LONGEVITY))
			.andExpect(jsonPath("$.sillageRatings").value(SILLAGE))
			.andExpect(jsonPath("$.reviewCnt").value(REVIEW_CNT))
			.andExpect(jsonPath("$.notes.length()").value(3))
			.andDo(print());

	}

	@Test
	@DisplayName("유효하지 않은 UUID로 접근 시 404 에러를 반환한다.")
	void given_InvalidUUID_When_GetPerfume_Then_404Fail() throws Exception {
		//given
		given(perfumeService.getPerfumeWithNotesAndBrand(NOT_EXIST_UUID)).willThrow(new EntityNotFoundException(
			ErrorCode.PERFUME_NOT_FOUND, NOT_EXIST_UUID));

		//when
		mockMvc.perform(get("/perfumes/" + NOT_EXIST_UUID))
			.andExpect(status().isNotFound())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").exists())
			.andExpect(jsonPath("$.errorMessage").exists())
			.andDo(print());
	}

	@Test
	@DisplayName("전달된 개수만큼의 향수를 조회한다.")
	void given_Amount_When_GetPerfumeByOverall_Then_Success() throws Exception {
		//given
		given(perfumeService.getPerfumeByOverall(3)).willReturn(List.of(PERFUME_DTO, PERFUME_DTO, PERFUME_DTO));

		//when
		mockMvc.perform(get("/perfumes/best").param("amount", String.valueOf(3)))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()").value(3))
			.andExpect(jsonPath("$[0].uuid").value(PERFUME_UUID.toString()))
			.andExpect(jsonPath("$[0].brandId").value(BRAND.getId()))
			.andExpect(jsonPath("$[0].brandName").value(BRAND.getBrandNameEng()))
			.andExpect(jsonPath("$[0].perfumeName").value(PERFUME_NAME))
			.andExpect(jsonPath("$[0].overallRatings").value(OVERALL))
			.andExpect(jsonPath("$[0].longevityRatings").value(LONGEVITY))
			.andExpect(jsonPath("$[0].sillageRatings").value(SILLAGE))
			.andExpect(jsonPath("$[0].reviewCnt").value(REVIEW_CNT))
			.andDo(print());
	}

	@Test
	@DisplayName("전달된 개수만큼의 향수를 조회한다.")
	void given_Amount_When_GetRandomPerfume_Then_Success() throws Exception {
		//given
		given(perfumeService.getRandomPerfume(3)).willReturn(List.of(PERFUME_DTO, PERFUME_DTO, PERFUME_DTO));

		//when
		mockMvc.perform(get("/perfumes/random").param("amount", String.valueOf(3)))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()").value(3))
			.andExpect(jsonPath("$[0].uuid").value(PERFUME_UUID.toString()))
			.andExpect(jsonPath("$[0].brandId").value(BRAND.getId()))
			.andExpect(jsonPath("$[0].brandName").value(BRAND.getBrandNameEng()))
			.andExpect(jsonPath("$[0].perfumeName").value(PERFUME_NAME))
			.andExpect(jsonPath("$[0].overallRatings").value(OVERALL))
			.andExpect(jsonPath("$[0].longevityRatings").value(LONGEVITY))
			.andExpect(jsonPath("$[0].sillageRatings").value(SILLAGE))
			.andExpect(jsonPath("$[0].reviewCnt").value(REVIEW_CNT))
			.andDo(print());
	}

	@Test
	@DisplayName("전달된 개수만큼의 향수를 조회한다.")
	void given_ValidBrand_When_SearchByBrand_Then_Success() throws Exception {
		//given
		List<PerfumeResponse> contents = List.of(PERFUME_DTO, PERFUME_DTO, PERFUME_DTO);
		int page = 0, size = 3;
		boolean hasNext = true;
		Pageable pageable = PageRequest.of(page, size);

		given(perfumeService.getPerfumeByBrand(BRAND.getBrandNameEng(), pageable))
			.willReturn(new SliceImpl<>(contents, pageable, hasNext));

		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("brand", BRAND.getBrandNameEng());
		parameters.add("page", String.valueOf(page));
		parameters.add("size", String.valueOf(size));

		//when
		mockMvc.perform(get("/perfumes").params(parameters))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content.length()").value(3))
			.andExpect(jsonPath("$.content[0].uuid").value(PERFUME_UUID.toString()))
			.andExpect(jsonPath("$.content[0].brandId").value(BRAND.getId()))
			.andExpect(jsonPath("$.content[0].brandName").value(BRAND.getBrandNameEng()))
			.andExpect(jsonPath("$.content[0].perfumeName").value(PERFUME_NAME))
			.andExpect(jsonPath("$.content[0].overallRatings").value(OVERALL))
			.andExpect(jsonPath("$.content[0].longevityRatings").value(LONGEVITY))
			.andExpect(jsonPath("$.content[0].sillageRatings").value(SILLAGE))
			.andExpect(jsonPath("$.content[0].reviewCnt").value(REVIEW_CNT))
			.andExpect(jsonPath("$.pageable.pageNumber").value(0))
			.andExpect(jsonPath("$.pageable.pageSize").value(3))
			.andExpect(jsonPath("$.first").value(true))
			.andExpect(jsonPath("$.last").value(false))
			.andDo(print());
	}

}
