package com.android.mynotes.domain.commands;

import com.android.mynotes.domain.entities.Note;
import com.android.mynotes.data.NotesRepository;

/**
 * A concrete command implementation for editing an existing note.
 * Executes by adding (or updating) the note with new data,
 * and undoes by reverting to the old note data.
 */
public class EditNoteCommand implements Command {

    private final NotesRepository repository;
    private final Note oldNote;
    private final Note newNote;

    /**
     * Constructs an EditNoteCommand with the specified repository,
     * the old (existing) note, and the new (updated) note.
     *
     * @param repository The {@link NotesRepository} handling data persistence.
     * @param oldNote    The existing {@link Note} before changes.
     * @param newNote    The updated {@link Note} after changes.
     */
    public EditNoteCommand(NotesRepository repository, Note oldNote, Note newNote) {
        this.repository = repository;
        this.oldNote = oldNote;
        this.newNote = newNote;
    }

    /**
     * Executes the edit operation by adding (or updating) the new note.
     * Depending on repository implementation, this may replace the old note.
     */
    @Override
    public void execute() {
        repository.addNote(newNote);
    }

    /**
     * Undoes the edit operation by restoring the old note.
     * Depending on repository implementation, this may overwrite the new note.
     */
    @Override
    public void undo() {
        repository.addNote(oldNote);
    }
}