package com.android.mynotes.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.android.mynotes.domain.entities.Note;

import java.util.List;

/**
 * Data Access Object (DAO) for performing database operations on the `notes` table.
 * This interface provides methods to query, insert, and delete notes in the database.
 */
@Dao
public interface NoteDao {

    /**
     * Retrieves all notes from the database ordered by their ID in descending order.
     *
     * @return A {@link LiveData} list of {@link Note} objects.
     */
    @Query("SELECT * FROM notes ORDER BY id DESC")
    LiveData<List<Note>> getAllNotes();

    /**
     * Inserts a new note into the database or replaces an existing one if there is a conflict.
     *
     * @param note The {@link Note} object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    /**
     * Deletes a specified note from the database.
     *
     * @param note The {@link Note} object to be deleted.
     */
    @Delete
    void deleteNote(Note note);
}