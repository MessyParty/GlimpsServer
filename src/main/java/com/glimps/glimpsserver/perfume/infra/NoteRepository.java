package com.glimps.glimpsserver.perfume.infra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.glimps.glimpsserver.perfume.domain.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

	@Query("select n from Note n where n.engName = :eng or n.korName = :kor")
	Optional<Note> findByName(String eng, String kor);

}
