package com.android.mynotes.domain.commands;

import com.android.mynotes.domain.Note;
import com.android.mynotes.data.NotesRepository;

public class DeleteNoteCommand implements Command {
    private final NotesRepository repository;
    private final Note note;

    public DeleteNoteCommand(NotesRepository repository, Note note) {
        this.repository = repository;
        this.note = note;
    }

    @Override
    public void execute() {
        repository.deleteNote(note);
    }

    @Override
    public void undo() {
        repository.addNote(note);
    }
}