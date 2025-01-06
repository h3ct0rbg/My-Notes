package com.android.mynotes.domain.commands;

import com.android.mynotes.domain.Note;
import com.android.mynotes.data.NotesRepository;

public class EditNoteCommand implements Command {
    private final NotesRepository repository;
    private final Note oldNote;
    private final Note newNote;

    public EditNoteCommand(NotesRepository repository, Note oldNote, Note newNote) {
        this.repository = repository;
        this.oldNote = oldNote;
        this.newNote = newNote;
    }

    @Override
    public void execute() {
        repository.addNote(newNote);
    }

    @Override
    public void undo() {
        repository.addNote(oldNote);
    }
}