package com.glimps.glimpsserver.perfume.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.perfume.application.BrandService;
import com.glimps.glimpsserver.perfume.dto.BrandResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Brand", description = "브랜드 조회 API")
@RequestMapping("${api.prefix}")
@RestController
public class BrandController {

	private final BrandService brandService;

	public BrandController(BrandService brandService) {
		this.brandService = brandService;
	}


	@Tag(name = "Brand")
	@Operation(summary = "브랜드 전체 조회 API", description = "인증 불필요, 모든 브랜드 정보를 조회합니다.")
	@GetMapping("/brands")
	public List<BrandResponse> getAllBrands() {
		return brandService.getAllBrands();
	}


}
