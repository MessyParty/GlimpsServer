package com.glimps.glimpsserver.perfume.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.glimps.glimpsserver.perfume.dto.CreateNoteRequest;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notes")
@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String engName;
    private String korName;

    public static Note createNote(CreateNoteRequest createNoteRequest) {
        return Note.builder()
            .engName(createNoteRequest.getEngName())
            .korName(createNoteRequest.getKorName())
            .build();
    }

    @Builder
    public Note(String engName, String korName) {
        this.engName = engName;
        this.korName = korName;
    }
}
