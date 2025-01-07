package com.android.mynotes.data;

import androidx.lifecycle.LiveData;

import com.android.mynotes.domain.entities.Note;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Repository class for managing data operations for notes.
 * Acts as a single source of truth for accessing the database and provides
 * a clean API for the ViewModel to interact with the data layer.
 */
public class NotesRepository {

    private final NotesDatabase database;

    /**
     * Constructs a NotesRepository with the specified database.
     *
     * @param database The {@link NotesDatabase} instance used for accessing the data.
     */
    public NotesRepository(NotesDatabase database) {
        this.database = database;
    }

    /**
     * Retrieves all notes stored in the database.
     * Returns a {@link LiveData} object that observes changes in the data.
     *
     * @return A LiveData list of {@link Note} objects.
     */
    public LiveData<List<Note>> getAllNotes() {
        return database.noteDao().getAllNotes();
    }

    /**
     * Adds a new note to the database. This operation is performed on a
     * background thread to avoid blocking the UI.
     *
     * @param note The {@link Note} to be added.
     */
    public void addNote(Note note) {
        Executors.newSingleThreadExecutor().execute(() -> database.noteDao().insertNote(note));
    }

    /**
     * Deletes a specified note from the database. This operation is performed on a
     * background thread to avoid blocking the UI.
     *
     * @param note The {@link Note} to be deleted.
     */
    public void deleteNote(Note note) {
        Executors.newSingleThreadExecutor().execute(() -> database.noteDao().deleteNote(note));
    }
}