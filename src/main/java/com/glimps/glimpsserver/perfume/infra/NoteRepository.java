package com.glimps.glimpsserver.perfume.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glimps.glimpsserver.perfume.domain.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
