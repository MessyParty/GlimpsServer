package com.glimps.glimpsserver.perfume.presentation;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.perfume.application.PerfumeService;
import com.glimps.glimpsserver.perfume.dto.PerfumeResponse;
import com.glimps.glimpsserver.perfume.dto.PerfumeSearchCondition;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Perfume", description = "향수 조회 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/perfumes")
public class PerfumeController {

	private final PerfumeService perfumeService;

	@Tag(name = "Perfume")
	@Operation(summary = "UUID 향수 단건 조회 API", description = "인증 불필요, 노트와 브랜드 전부를 불러옵니다.")
	@ApiImplicitParam(name = "uuid", value = "향수 UUID", required = true, dataType = "string", paramType = "path")
	@GetMapping("/{uuid}")
	public PerfumeResponse getByUUID(@PathVariable UUID uuid) {
		return perfumeService.getPerfumeWithNotesAndBrand(uuid);
	}

	@Tag(name = "Perfume")
	@Operation(summary = "향수 브랜드 조회 API", description = "인증 불필요, 노트 정보는 갖고 오지 않습니다. \"브랜드명%\" 조회입니다. ")
	@GetMapping
	public Slice<PerfumeResponse> searchByBrand(@RequestParam("brand") String brandName, Pageable pageable) {
		return perfumeService.getPerfumeByBrand(brandName, pageable);
	}

	@Tag(name = "Perfume")
	@Operation(summary = "향수 랜덤 조회 API", description = "인증 불필요, 노트 정보는 갖고 오지 않습니다. 개수는 최대 10개로 제한됩니다.")
	@GetMapping("/random")
	public List<PerfumeResponse> getRandom(@RequestParam @NotNull Integer amount) {
		return perfumeService.getRandomPerfume(amount);
	}

	@Tag(name = "Perfume")
	@Operation(summary = "향수 총점 순 조회 API", description = "인증 불필요, 노트 정보는 갖고 오지 않습니다. 개수는 최대 10개로 제한됩니다.")
	@GetMapping("/best")
	public List<PerfumeResponse> getBest(@RequestParam @NotNull Integer amount) {
		return perfumeService.getPerfumeByOverall(amount);
	}

	@Tag(name = "Perfume")
	@Operation(summary = "향수 검색 API", description = "인증 불필요, brand, perfume 으로 조회시 정상적인 노트 정보를 가지고 오지만 "
		+ "note로 조회시 매칭되는 노트만 가지고 옵니다.")
	@GetMapping("/search")
	public Slice<PerfumeResponse> search(PerfumeSearchCondition condition, Pageable pageable) {
		return perfumeService.search(condition, pageable);
	}

	/**
	 * 개발 환경용 API
	 *
	 * @return All Perfumes
	 */
	@Tag(name = "Perfume")
	@Operation(summary = "향수 전체 조회 API", description = "개발용 API입니다.")
	@GetMapping("/all")
	public List<PerfumeResponse> getAll() {
		return perfumeService.getAll();
	}

	/**
	 * 향수 노트 추가 API
	 */
	@Tag(name = "Test", description = "Test API")
	@Operation(summary = "노트 추가 API", description = "노트 추가 API")
	@GetMapping("/notes")
	public PerfumeResponse addNote(@RequestParam UUID uuid, @RequestParam String eng, @RequestParam String kor) {
		return perfumeService.addNote(uuid, eng.toUpperCase(), kor);
	}

}
