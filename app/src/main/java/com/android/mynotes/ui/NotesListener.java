package com.android.mynotes.ui;

import com.android.mynotes.domain.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
