package com.android.mynotes.commands;

import com.android.mynotes.database.NotesDatabase;
import com.android.mynotes.entities.Note;

public class AddNoteCommand implements Command {
    private final NotesDatabase database;
    private final Note note;

    public AddNoteCommand(NotesDatabase database, Note note) {
        this.database = database;
        this.note = note;
    }

    @Override
    public void execute() {
        database.noteDao().insertNote(note);
    }

    @Override
    public void undo() {
        database.noteDao().deleteNote(note);
    }
}