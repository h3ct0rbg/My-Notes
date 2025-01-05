package com.android.mynotes.commands;

import com.android.mynotes.database.NotesDatabase;
import com.android.mynotes.entities.Note;

public class DeleteNoteCommand implements Command {
    private final NotesDatabase database;
    private final Note note;

    public DeleteNoteCommand(NotesDatabase database, Note note) {
        this.database = database;
        this.note = note;
    }

    @Override
    public void execute() {
        database.noteDao().deleteNote(note);
    }

    @Override
    public void undo() {
        database.noteDao().insertNote(note);
    }
}