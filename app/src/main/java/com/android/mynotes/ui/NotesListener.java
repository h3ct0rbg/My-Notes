package com.android.mynotes.ui;

import com.android.mynotes.domain.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
