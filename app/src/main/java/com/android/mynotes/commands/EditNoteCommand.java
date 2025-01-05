package com.android.mynotes.commands;

import com.android.mynotes.database.NotesDatabase;
import com.android.mynotes.entities.Note;

public class EditNoteCommand implements Command {
    private final NotesDatabase database;
    private final Note oldNote;
    private final Note newNote;

    public EditNoteCommand(NotesDatabase database, Note oldNote, Note newNote) {
        this.database = database;
        this.oldNote = oldNote;
        this.newNote = newNote;
    }

    @Override
    public void execute() {
        database.noteDao().insertNote(newNote);
    }

    @Override
    public void undo() {
        database.noteDao().insertNote(oldNote);
    }
}