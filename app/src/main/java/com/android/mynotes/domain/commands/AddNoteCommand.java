package com.android.mynotes.domain.commands;

import com.android.mynotes.domain.entities.Note;
import com.android.mynotes.data.NotesRepository;

public class AddNoteCommand implements Command {
    private final NotesRepository repository;
    private final Note note;

    public AddNoteCommand(NotesRepository repository, Note note) {
        this.repository = repository;
        this.note = note;
    }

    @Override
    public void execute() {
        repository.addNote(note);
    }

    @Override
    public void undo() {
        repository.deleteNote(note);
    }
}