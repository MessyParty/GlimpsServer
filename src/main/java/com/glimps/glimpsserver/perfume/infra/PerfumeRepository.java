package com.glimps.glimpsserver.perfume.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.glimps.glimpsserver.perfume.domain.Perfume;

public interface PerfumeRepository extends JpaRepository<Perfume, Long> {

	Optional<Perfume> findByUuid(UUID uuid);

	@Query("select p from Perfume p "
		+ "left join fetch p.brand b "
		+ "left join fetch p.perfumeNotes pn "
		+ "left join fetch pn.note "
		+ "where p.uuid = :uuid")
	Optional<Perfume> findPerfumeWithEntities(UUID uuid);

}
