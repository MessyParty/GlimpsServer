package com.glimps.glimpsserver.perfume.presentation;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import org.springframework.data.domain.Page;
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

	@GetMapping
	public PerfumeResponse search(@RequestParam("brand") String brandName, Page page) {
		// 쿼리 dsl 동적 쿼리
		return null;
	}

	@GetMapping("/random")
	public PerfumeResponse getRandom(@RequestParam Integer amount) {
		return null;
	}

	@ApiImplicitParam(name = "uuid", value = "향수 uuid", required = true, dataType = "string", paramType = "path")
	@GetMapping("/{uuid}")
	public PerfumeResponse getByUUID(@PathVariable UUID uuid) {
		return perfumeService.getPerfumeWithNotesAndBrand(uuid);
	}

	@GetMapping("/best")
	public PerfumeResponse searchBest(@RequestParam @NotBlank Integer amount) {
		return null;
	}

	/**
	 * MVP 아님
	 */
	@GetMapping("/hashtag")
	public PerfumeResponse searchBy(@RequestParam @NotBlank String tag) {
		return null;
	}

	@GetMapping("/all")
	public List<PerfumeResponse> getAll() {
		return perfumeService.getAll();
	}





}
