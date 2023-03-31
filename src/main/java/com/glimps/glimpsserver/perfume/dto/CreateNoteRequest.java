package com.glimps.glimpsserver.perfume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateNoteRequest {

	private String EngName;
	private String KorName;

}
