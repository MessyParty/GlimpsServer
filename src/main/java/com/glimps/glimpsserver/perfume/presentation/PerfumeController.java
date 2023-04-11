package com.glimps.glimpsserver.perfume.presentation;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.perfume.application.PerfumeService;
import com.glimps.glimpsserver.perfume.dto.PerfumeResponse;

import io.swagger.annotations.ApiImplicitParam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/perfumes")
public class PerfumeController {

	private final PerfumeService perfumeService;

	@ApiImplicitParam(name = "uuid", value = "향수 uuid", required = true, dataType = "string", paramType = "path")
	@GetMapping("/{uuid}")
	public PerfumeResponse getByUUID(@PathVariable UUID uuid) {
		return perfumeService.getPerfumeWithNotesAndBrand(uuid);
	}

	@GetMapping
	public Page<PerfumeResponse> searchByBrand(@RequestParam("brand") String brandName, Pageable pageable) {
		return perfumeService.getPerfumeByBrand(brandName, pageable);
	}

	@GetMapping("/random")
	public List<PerfumeResponse> getRandom(@RequestParam @NotNull Integer amount) {
		return perfumeService.getRandomPerfume(amount);
	}

	@GetMapping("/best")
	public List<PerfumeResponse> getBest(@RequestParam @NotNull Integer amount) {
		return perfumeService.getPerfumeByOverall(amount);
	}

	@GetMapping("/search")
	public Page<PerfumeResponse> search(
		@RequestParam("brand") String brandName,
		@RequestParam("name") String perfumeName,
		@RequestParam String note,
		Pageable pageable
	) {
		return null;
	}

	/**
	 * 개발 환경용 API
	 * @return All Perfumes
	 */
	@GetMapping("/all")
	public List<PerfumeResponse> getAll() {
		return perfumeService.getAll();
	}


}
