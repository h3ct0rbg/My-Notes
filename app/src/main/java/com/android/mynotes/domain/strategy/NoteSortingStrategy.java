package com.android.mynotes.domain.strategy;

import com.android.mynotes.domain.entities.Note;

import java.util.List;

public interface NoteSortingStrategy {
    List<Note> sort(List<Note> notes);
}