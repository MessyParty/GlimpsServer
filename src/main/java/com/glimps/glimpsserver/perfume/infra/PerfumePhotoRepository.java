package com.glimps.glimpsserver.perfume.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glimps.glimpsserver.perfume.domain.PerfumePhoto;

public interface PerfumePhotoRepository extends JpaRepository<PerfumePhoto, Long> {
}
