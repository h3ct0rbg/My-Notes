package com.android.mynotes.listeners;

import com.android.mynotes.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
