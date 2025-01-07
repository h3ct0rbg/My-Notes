package com.android.mynotes.domain.commands;

import com.android.mynotes.domain.entities.Note;
import com.android.mynotes.data.NotesRepository;

/**
 * A concrete command implementation for deleting a note.
 * Executes by removing the note from the repository and
 * undoes by re-adding the same note.
 */
public class DeleteNoteCommand implements Command {

    private final NotesRepository repository;
    private final Note note;

    /**
     * Constructs a DeleteNoteCommand with the specified repository and note.
     *
     * @param repository The {@link NotesRepository} handling data persistence.
     * @param note       The {@link Note} to be deleted.
     */
    public DeleteNoteCommand(NotesRepository repository, Note note) {
        this.repository = repository;
        this.note = note;
    }

    /**
     * Executes the delete operation on the specified note.
     * Removes the note from the repository.
     */
    @Override
    public void execute() {
        repository.deleteNote(note);
    }

    /**
     * Undoes the delete operation by re-adding the note
     * to the repository.
     */
    @Override
    public void undo() {
        repository.addNote(note);
    }
}