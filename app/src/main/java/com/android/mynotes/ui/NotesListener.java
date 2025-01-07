package com.android.mynotes.ui;

import com.android.mynotes.domain.entities.Note;

/**
 * Listener interface for handling note-related events, such as when a note is clicked.
 */
public interface NotesListener {

    /**
     * Called when a note is clicked in the list.
     *
     * @param note     The note that was clicked.
     * @param position The position of the clicked note in the list.
     */
    void onNoteClicked(Note note, int position);
}