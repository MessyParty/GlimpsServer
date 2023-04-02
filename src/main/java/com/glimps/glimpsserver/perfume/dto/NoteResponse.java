package com.glimps.glimpsserver.perfume.dto;

import com.glimps.glimpsserver.perfume.domain.Note;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoteResponse {
	private Long id;
	private String korName;
	private String engName;

	public static NoteResponse of(Note note) {
		return NoteResponse.builder()
			.id(note.getId())
			.engName(note.getEngName())
			.korName(note.getKorName())
			.build();
	}
}
