package com.glimps.glimpsserver.perfume.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glimps.glimpsserver.perfume.domain.PerfumeNote;

public interface PerfumeNoteRepository extends JpaRepository<PerfumeNote, Long> {
}
