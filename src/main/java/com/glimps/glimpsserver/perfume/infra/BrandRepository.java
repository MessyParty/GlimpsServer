package com.glimps.glimpsserver.perfume.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glimps.glimpsserver.perfume.domain.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {

}
