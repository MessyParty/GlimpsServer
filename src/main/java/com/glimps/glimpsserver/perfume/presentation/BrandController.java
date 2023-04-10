package com.glimps.glimpsserver.perfume.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.perfume.application.BrandService;
import com.glimps.glimpsserver.perfume.dto.BrandResponse;

@RequestMapping("${api.prefix}")
@RestController
public class BrandController {

	private final BrandService brandService;

	public BrandController(BrandService brandService) {
		this.brandService = brandService;
	}


	@GetMapping("/brands")
	public List<BrandResponse> getAllBrands() {
		return brandService.getAllBrands();
	}


}
