package com.glimps.glimpsserver.perfume.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glimps.glimpsserver.perfume.dto.BrandResponse;
import com.glimps.glimpsserver.perfume.infra.BrandRepository;

@Service
@Transactional(readOnly = true)
public class BrandService {

	private final BrandRepository brandRepository;

	public BrandService(BrandRepository brandRepository) {
		this.brandRepository = brandRepository;
	}

	public List<BrandResponse> getAllBrands() {
		return brandRepository.findAll().stream().map((b) -> new BrandResponse(b.getId(),b.getBrandName()))
				.collect(Collectors.toList());
	}


}
