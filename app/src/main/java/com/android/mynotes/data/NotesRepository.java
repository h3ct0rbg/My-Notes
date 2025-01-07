package com.android.mynotes.data;

import androidx.lifecycle.LiveData;

import com.android.mynotes.domain.entities.Note;

import java.util.List;
import java.util.concurrent.Executors;

public class NotesRepository {

    private final NotesDatabase database;

    public NotesRepository(NotesDatabase database) {
        this.database = database;
    }

    public LiveData<List<Note>> getAllNotes() {
        return database.noteDao().getAllNotes();
    }

    public void addNote(Note note) {
        Executors.newSingleThreadExecutor().execute(() -> database.noteDao().insertNote(note));
    }

    public void deleteNote(Note note) {
        Executors.newSingleThreadExecutor().execute(() -> database.noteDao().deleteNote(note));
    }
}