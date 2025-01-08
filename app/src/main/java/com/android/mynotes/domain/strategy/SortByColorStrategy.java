package com.android.mynotes.domain.strategy;

import com.android.mynotes.domain.entities.Note;

import java.util.Comparator;
import java.util.List;

public class SortByColorStrategy implements NoteSortingStrategy {
    @Override
    public List<Note> sort(List<Note> notes) {
        notes.sort(Comparator.comparing(Note::getColor));
        return notes;
    }
}
