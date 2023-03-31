package com.glimps.glimpsserver.perfume.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "perfume_notes")
@Entity
public class PerfumeNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfume_id")
    private Perfume perfume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    public static PerfumeNote addNote(Perfume perfume, Note note) {
        return PerfumeNote.builder()
            .perfume(perfume)
            .note(note)
            .build();
    }

    @Builder
    public PerfumeNote(Perfume perfume, Note note) {
        this.perfume = perfume;
        this.note = note;
    }
}
