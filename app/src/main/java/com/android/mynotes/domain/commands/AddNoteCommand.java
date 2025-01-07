package com.android.mynotes.domain.commands;

import com.android.mynotes.domain.entities.Note;
import com.android.mynotes.data.NotesRepository;

/**
 * A concrete command implementation for adding a new note.
 * Executes by adding the note to the repository and
 * undoes by removing the same note.
 */
public class AddNoteCommand implements Command {

    private final NotesRepository repository;
    private final Note note;

    /**
     * Constructs an AddNoteCommand with the specified repository and note.
     *
     * @param repository The {@link NotesRepository} handling data persistence.
     * @param note       The {@link Note} to be added.
     */
    public AddNoteCommand(NotesRepository repository, Note note) {
        this.repository = repository;
        this.note = note;
    }

    /**
     * Executes the add operation by inserting the note into the repository.
     */
    @Override
    public void execute() {
        repository.addNote(note);
    }

    /**
     * Undoes the add operation by removing the note from the repository.
     */
    @Override
    public void undo() {
        repository.deleteNote(note);
    }
}