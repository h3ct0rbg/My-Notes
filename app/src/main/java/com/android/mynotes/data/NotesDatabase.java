package com.android.mynotes.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.mynotes.domain.entities.Note;

/**
 * Singleton class representing the Room database for the `notes` table.
 * Provides access to the database instance and its DAO.
 */
@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {

    /**
     * Singleton instance of the NotesDatabase.
     */
    private static NotesDatabase notesDatabase;

    /**
     * Retrieves the singleton instance of the NotesDatabase.
     * If the instance does not exist, it initializes the database.
     *
     * @param context The application context.
     * @return The singleton instance of {@link NotesDatabase}.
     */
    public static synchronized NotesDatabase getDataBase(Context context) {
        if (notesDatabase == null) {
            notesDatabase = Room.databaseBuilder(
                    context,
                    NotesDatabase.class,
                    "notes db"
            ).build();
        }
        return notesDatabase;
    }

    /**
     * Provides access to the {@link NoteDao} for performing database operations.
     *
     * @return An instance of {@link NoteDao}.
     */
    public abstract NoteDao noteDao();

}