package com.glimps.glimpsserver.perfume.infra;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.glimps.glimpsserver.perfume.domain.Perfume;

public interface PerfumeRepository extends JpaRepository<Perfume, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Perfume> findByUuid(UUID uuid);
}
